package ma.dentalTech.configuration;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;

public class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    // Pour éviter la récursion / cycles simples
    private static final Set<String> creating = new HashSet<>();

    static {
        loadBeans("config/beans.properties");
    }

    private static void loadBeans(String path) {
        try (InputStream configFile = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {

            if (configFile == null) {
                throw new RuntimeException("Fichier introuvable: " + path);
            }

            Properties props = new Properties();
            props.load(configFile);

            // 1) ordre: repos -> services -> controllers
            List<String> keys = new ArrayList<>(props.stringPropertyNames());
            keys.sort(Comparator.comparingInt(ApplicationContext::beanPriority));

            // 2) instanciation de tous les beans
            for (String beanName : keys) {
                String className = props.getProperty(beanName).trim();
                Object bean = createBean(className);

                contextByName.put(beanName, bean);

                // 1) Enregistrement par classe concrète
                context.putIfAbsent(bean.getClass(), bean);

                // 2) Enregistrement par interfaces
                for (Class<?> itf : bean.getClass().getInterfaces()) {
                    if (!itf.getName().startsWith("java.")) {
                        context.putIfAbsent(itf, bean);
                    }
                }

                // 3) Enregistrement par super-classes
                Class<?> sup = bean.getClass().getSuperclass();
                while (sup != null && sup != Object.class) {
                    context.putIfAbsent(sup, bean);
                    sup = sup.getSuperclass();
                }
            }

            System.out.println("✅ ApplicationContext: " + contextByName.size() + " beans chargés.");
        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement ApplicationContext", e);
        }
    }

    private static int beanPriority(String key) {
        String k = key.toLowerCase(Locale.ROOT);
        if (k.contains("repo") || k.contains("repository") || k.contains("dao")) return 1;
        if (k.contains("service")) return 2;
        if (k.contains("controller")) return 3;
        return 9;
    }

    private static Object createBean(String className) throws Exception {
        if (creating.contains(className)) {
            // cycle détecté : on stoppe net avec message clair
            throw new RuntimeException("Cycle de dépendances détecté avec: " + className);
        }

        Class<?> clazz = Class.forName(className);

        // si déjà créé (par nom)
        for (Object existing : contextByName.values()) {
            if (clazz.isInstance(existing)) return existing;
        }

        creating.add(className);

        try {
            Constructor<?>[] ctors = clazz.getDeclaredConstructors();
            Arrays.sort(ctors, (a, b) -> Integer.compare(b.getParameterCount(), a.getParameterCount()));

            for (Constructor<?> ctor : ctors) {
                Class<?>[] paramTypes = ctor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];

                boolean ok = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    Object dep = getBean(paramTypes[i]);
                    if (dep == null) {
                        ok = false;
                        break;
                    }
                    args[i] = dep;
                }

                if (ok) {
                    ctor.setAccessible(true);
                    return ctor.newInstance(args);
                }
            }

            // fallback: constructeur vide si existe
            try {
                Constructor<?> noArg = clazz.getDeclaredConstructor();
                noArg.setAccessible(true);
                return noArg.newInstance();
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("Aucun constructeur injectable trouvé pour: " + className);
            }
        } finally {
            creating.remove(className);
        }
    }

    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        Object bean = context.get(beanClass);

        if (bean != null) return beanClass.cast(bean);

        // chercher assignable
        for (Object b : context.values()) {
            if (beanClass.isInstance(b)) return beanClass.cast(b);
        }
        return null; // ✅ IMPORTANT
    }
}

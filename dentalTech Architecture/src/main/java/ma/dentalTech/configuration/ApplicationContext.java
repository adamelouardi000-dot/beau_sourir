package ma.dentalTech.configuration;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Mini IoC container (sans framework).
 * - Charge config/beans.properties
 * - Crée les instances une seule fois
 * - Permet de récupérer par nom ou par type
 */
public final class ApplicationContext {

    private static final Map<Class<?>, Object> context = new HashMap<>();
    private static final Map<String, Object> contextByName = new HashMap<>();

    private static final String BEANS_PROPS = "config/beans.properties";

    static {
        loadBeans();
    }

    private ApplicationContext() { }

    private static void loadBeans() {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(BEANS_PROPS);

        if (in == null) {
            System.err.println("Erreur : fichier introuvable -> " + BEANS_PROPS);
            return;
        }

        Properties props = new Properties();
        try {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de lire " + BEANS_PROPS, e);
        }

        // 1) on garde tout ce qu'il faut créer
        Map<String, String> toCreate = new LinkedHashMap<>();
        for (String key : props.stringPropertyNames()) {
            String className = props.getProperty(key);
            if (className != null && !className.isBlank()) {
                toCreate.put(key.trim(), className.trim());
            }
        }

        // 2) création en plusieurs passes : d'abord no-arg, puis constructeurs avec deps
        Set<String> created = new HashSet<>();
        boolean progress;

        do {
            progress = false;

            for (Map.Entry<String, String> e : toCreate.entrySet()) {
                String beanName = e.getKey();
                String className = e.getValue();

                if (created.contains(beanName)) continue;

                Object instance = tryCreateInstance(className);
                if (instance != null) {
                    registerBean(beanName, instance);
                    created.add(beanName);
                    progress = true;
                }
            }

        } while (progress);

        // 3) si certains restent non créés -> message clair
        for (String beanName : toCreate.keySet()) {
            if (!created.contains(beanName)) {
                System.err.println("⚠️ Bean non créé (dépendances non résolues ?) : "
                        + beanName + " -> " + toCreate.get(beanName));
            }
        }
    }

    /**
     * Tente de créer une instance :
     * - 1) constructeur vide
     * - 2) constructeur avec 1..N paramètres déjà présents dans le context
     */
    private static Object tryCreateInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);

            // 1) no-arg si possible
            try {
                Constructor<?> noArg = clazz.getDeclaredConstructor();
                noArg.setAccessible(true);
                return noArg.newInstance();
            } catch (NoSuchMethodException ignored) {
                // on essaie les autres constructeurs
            }

            // 2) constructeurs avec dépendances déjà disponibles
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount)); // petit -> grand

            for (Constructor<?> ctor : constructors) {
                Class<?>[] paramTypes = ctor.getParameterTypes();
                Object[] args = new Object[paramTypes.length];

                boolean ok = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    Object dep = getBeanOrNull(paramTypes[i]);
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

            return null;

        } catch (Exception ex) {
            throw new RuntimeException("Erreur création bean: " + className, ex);
        }
    }

    private static void registerBean(String name, Object bean) {
        contextByName.put(name, bean);

        // enregistrer par classe
        context.put(bean.getClass(), bean);

        // enregistrer par interfaces (ex: PatientRepository.class)
        for (Class<?> itf : bean.getClass().getInterfaces()) {
            context.putIfAbsent(itf, bean);
        }

        // enregistrer aussi par superclasses si utile
        Class<?> sup = bean.getClass().getSuperclass();
        while (sup != null && sup != Object.class) {
            context.putIfAbsent(sup, bean);
            sup = sup.getSuperclass();
        }
    }

    private static Object getBeanOrNull(Class<?> type) {
        // 1) direct
        Object v = context.get(type);
        if (v != null) return v;

        // 2) compatible (cas où on a stocké une impl sous une autre clé)
        for (Map.Entry<Class<?>, Object> entry : context.entrySet()) {
            if (type.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    // ================= API =================

    public static Object getBean(String beanName) {
        return contextByName.get(beanName);
    }

    public static <T> T getBean(Class<T> beanClass) {
        Object bean = getBeanOrNull(beanClass);
        if (bean == null) return null;
        return beanClass.cast(bean);
    }
    public static void printLoadedBeans() {
        System.out.println("\n===== BEANS LOADED (by name) =====");
        contextByName.forEach((k, v) ->
                System.out.println(" - " + k + " -> " + v.getClass().getName())
        );

        System.out.println("\n===== BEANS LOADED (by type) =====");
        context.forEach((k, v) ->
                System.out.println(" - " + k.getName() + " -> " + v.getClass().getName())
        );
    }

}

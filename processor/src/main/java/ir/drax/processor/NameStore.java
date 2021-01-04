package ir.drax.processor;

import ir.drax.annotations.internal.BindingSuffix;

public final class NameStore {

    private NameStore() {
        // not to be instantiated in public
    }

    public static String getGeneratedClassName(String clsName) {
        return clsName + BindingSuffix.GENERATED_CLASS_SUFFIX;
    }

    public static class Package {
        public static final String ANDROID_VIEW = "android.view";
        public static final String CONTENT = "androidx.core.content";
        public static final String PACKAGE_MANAGER = "android.content.pm";
        public static final String ANDROID_CONTENT = "android.content";
        public static final String RESULT_LAUNCHER = "androidx.activity.result";
        public static final String RESULT_LAUNCHER_RESULT = "androidx.activity.result.contract";
        public static final String FRAGMENT = "androidx.fragment.app";
        public static final String WIDGET = "android.widget";
    }

    public static class Class {
        // Android
        public static final String ANDROID_VIEW = "View";
        public static final String ANDROID_VIEW_ON_CLICK_LISTENER = "OnClickListener";
    }

    public static class Method {
        // Android
        public static final String ANDROID_VIEW_ON_CLICK = "onClick";
        public static final String CONTEXT = "getContext";
        public static final String LAUNCHER_SETUP = "SetupLauncher";

        // Binder
        public static final String BIND_VIEWS = "bindViews";
        public static final String BIND_ON_CLICKS = "bindOnClicks";
        public static final String BIND = "bind";
    }

    public static class Variable {
        public static final String ANDROID_ACTIVITY = "activity";
        public static final String PERMISSION_RESULT_LAUNCHER = "permissionResultLauncher";
        public static final String ANDROID_VIEW = "view";
    }
}


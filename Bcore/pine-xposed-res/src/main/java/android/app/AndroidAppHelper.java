package android.app;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;
import static de.robv.android.xposed.XposedHelpers.setFloatField;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Resources;
import android.view.Display;
import black.android.app.BRActivityThread;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Contains various methods for information about the current app.
 *
 * <p>For historical reasons, this class is in the {@code android.app} package. It can't be moved
 * without breaking compatibility with existing modules.
 */
public final class AndroidAppHelper {
  private AndroidAppHelper() {}

  private static final Class<?> CLASS_RESOURCES_KEY;

  // Dreamland changed: remove unused field for sdk 24: HAS_IS_THEMEABLE and
  // HAS_THEME_CONFIG_PARAMETER
  //	private static final boolean HAS_IS_THEMEABLE;
  //	private static final boolean HAS_THEME_CONFIG_PARAMETER;

  static {
    // Dreamland changed: remove unreachable condition
    //		CLASS_RESOURCES_KEY = (Build.VERSION.SDK_INT < 19) ?
    //			  findClass("android.app.ActivityThread$ResourcesKey", null)
    //			: findClass("android.content.res.ResourcesKey", null);

    CLASS_RESOURCES_KEY = findClass("android.content.res.ResourcesKey", null);

    //		HAS_IS_THEMEABLE = findFieldIfExists(CLASS_RESOURCES_KEY, "mIsThemeable") != null;
    //		HAS_THEME_CONFIG_PARAMETER = HAS_IS_THEMEABLE && Build.VERSION.SDK_INT >= 21
    //				&& findMethodExactIfExists("android.app.ResourcesManager", null, "getThemeConfig") !=
    // null;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Map<Object, WeakReference> getResourcesMap(Object activityThread) {
    // Dreamland changed: remove unreachable condition
    //		if (Build.VERSION.SDK_INT >= 24) {
    //			Object resourcesManager = getObjectField(activityThread, "mResourcesManager");
    //			return (Map) getObjectField(resourcesManager, "mResourceImpls");
    //		} else if (Build.VERSION.SDK_INT >= 19) {
    //			Object resourcesManager = getObjectField(activityThread, "mResourcesManager");
    //			return (Map) getObjectField(resourcesManager, "mActiveResources");
    //		} else {
    //			return (Map) getObjectField(activityThread, "mActiveResources");
    //		}
    Object resourcesManager = getObjectField(activityThread, "mResourcesManager");
    return (Map) getObjectField(resourcesManager, "mResourceImpls");
  }

  // Dreamland changed: remove unreachable methods
  //	/* For SDK 15 & 16 */
  //	private static Object createResourcesKey(String resDir, float scale) {
  //		try {
  //			if (HAS_IS_THEMEABLE)
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, scale, false);
  //			else
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, scale);
  //		} catch (Throwable t) {
  //			XposedBridge.log(t);
  //			return null;
  //		}
  //	}
  //
  //	/* For SDK 17 & 18 & 23 */
  //	private static Object createResourcesKey(String resDir, int displayId, Configuration
  // overrideConfiguration, float scale) {
  //		try {
  //			if (HAS_THEME_CONFIG_PARAMETER)
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, displayId, overrideConfiguration, scale,
  // false, null);
  //			else if (HAS_IS_THEMEABLE)
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, displayId, overrideConfiguration, scale,
  // false);
  //			else
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, displayId, overrideConfiguration, scale);
  //		} catch (Throwable t) {
  //			XposedBridge.log(t);
  //			return null;
  //		}
  //	}
  //
  //	/* For SDK 19 - 22 */
  //	private static Object createResourcesKey(String resDir, int displayId, Configuration
  // overrideConfiguration, float scale, IBinder token) {
  //		try {
  //			if (HAS_THEME_CONFIG_PARAMETER)
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, displayId, overrideConfiguration, scale,
  // false, null, token);
  //			else if (HAS_IS_THEMEABLE)
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, displayId, overrideConfiguration, scale,
  // false, token);
  //			else
  //				return newInstance(CLASS_RESOURCES_KEY, resDir, displayId, overrideConfiguration, scale,
  // token);
  //		} catch (Throwable t) {
  //			XposedBridge.log(t);
  //			return null;
  //		}
  //	}

  // Dreamland changed: createResourcesKey be inlined now
  //	/* For SDK 24+ */
  //	private static Object createResourcesKey(String resDir, String[] splitResDirs, String[]
  // overlayDirs, String[] libDirs, int displayId, Configuration overrideConfiguration,
  // CompatibilityInfo compatInfo) {
  //		try {
  //			return newInstance(CLASS_RESOURCES_KEY, resDir, splitResDirs, overlayDirs, libDirs,
  // displayId, overrideConfiguration, compatInfo);
  //		} catch (Throwable t) {
  //			XposedBridge.log(t);
  //			return null;
  //		}
  //	}

  /** @hide */
  public static void addActiveResource(
      String resDir, float scale, boolean isThemeable, Resources resources) {
    addActiveResource(resDir, resources);
  }

  /** @hide */
  public static void addActiveResource(String resDir, Resources resources) {
    Object thread = BRActivityThread.get().currentActivityThread();
    if (thread == null) {
      return;
    }

    // Dreamland changed: remove unreachable conditions
    //		Object resourcesKey;
    //		if (Build.VERSION.SDK_INT >= 24) {
    //			CompatibilityInfo compatInfo = (CompatibilityInfo) newInstance(CompatibilityInfo.class);
    //			setFloatField(compatInfo, "applicationScale", resources.hashCode());
    //			resourcesKey = createResourcesKey(resDir, null, null, null, Display.DEFAULT_DISPLAY, null,
    // compatInfo);
    //		} else if (Build.VERSION.SDK_INT == 23) {
    //			resourcesKey = createResourcesKey(resDir, Display.DEFAULT_DISPLAY, null,
    // resources.hashCode());
    //		} else if (Build.VERSION.SDK_INT >= 19) {
    //			resourcesKey = createResourcesKey(resDir, Display.DEFAULT_DISPLAY, null,
    // resources.hashCode(), null);
    //		} else if (Build.VERSION.SDK_INT >= 17) {
    //			resourcesKey = createResourcesKey(resDir, Display.DEFAULT_DISPLAY, null,
    // resources.hashCode());
    //		} else {
    //			resourcesKey = createResourcesKey(resDir, resources.hashCode());
    //		}

    CompatibilityInfo compatInfo = (CompatibilityInfo) newInstance(CompatibilityInfo.class);
    setFloatField(compatInfo, "applicationScale", resources.hashCode());

    // Dreamland changed: inline createResourcesKey
    //		Object resourcesKey = createResourcesKey(resDir, null, null, null, Display.DEFAULT_DISPLAY,
    // null, compatInfo);
    Object resourcesKey;
    try {
      resourcesKey =
          newInstance(
              CLASS_RESOURCES_KEY,
              resDir,
              null,
              null,
              null,
              Display.DEFAULT_DISPLAY,
              null,
              compatInfo);
    } catch (Throwable e) {
      XposedBridge.log(e);
      return;
    }

    // Dreamland changed: remove useless judgments
    //		if (resourcesKey != null) {
    //			if (Build.VERSION.SDK_INT >= 24) {
    //				Object resImpl = getObjectField(resources, "mResourcesImpl");
    //				getResourcesMap(thread).put(resourcesKey, new WeakReference<>(resImpl));
    //			} else {
    //				getResourcesMap(thread).put(resourcesKey, new WeakReference<>(resources));
    //			}

    Object resImpl = getObjectField(resources, "mResourcesImpl");
    getResourcesMap(thread).put(resourcesKey, new WeakReference<>(resImpl));
    //		}
  }

  /** Returns the name of the current process. It's usually the same as the main package name. */
  public static String currentProcessName() {
    String processName = BRActivityThread.get().currentPackageName();
    if (processName == null) return "android";
    return processName;
  }

  /**
   * Returns information about the main application in the current process.
   *
   * <p>In a few cases, multiple apps might run in the same process, e.g. the SystemUI and the
   * Keyguard which both have {@code android:process="com.android.systemui"} set in their manifest.
   * In those cases, the first application that was initialized will be returned.
   */
  public static ApplicationInfo currentApplicationInfo() {
    Object am = BRActivityThread.get().currentActivityThread();
    if (am == null) return null;

    Object boundApplication = getObjectField(am, "mBoundApplication");
    if (boundApplication == null) return null;

    return (ApplicationInfo) getObjectField(boundApplication, "appInfo");
  }

  /**
   * Returns the Android package name of the main application in the current process.
   *
   * <p>In a few cases, multiple apps might run in the same process, e.g. the SystemUI and the
   * Keyguard which both have {@code android:process="com.android.systemui"} set in their manifest.
   * In those cases, the first application that was initialized will be returned.
   */
  public static String currentPackageName() {
    ApplicationInfo ai = currentApplicationInfo();
    return (ai != null) ? ai.packageName : "android";
  }

  /**
   * Returns the main {@link Application} object in the current process.
   *
   * <p>In a few cases, multiple apps might run in the same process, e.g. the SystemUI and the
   * Keyguard which both have {@code android:process="com.android.systemui"} set in their manifest.
   * In those cases, the first application that was initialized will be returned.
   */
  public static Application currentApplication() {
    return BRActivityThread.get().currentApplication();
  }

  /** @deprecated Use {@link XSharedPreferences} instead. */
  @SuppressWarnings("UnusedParameters")
  @Deprecated
  public static SharedPreferences getSharedPreferencesForPackage(
      String packageName, String prefFileName, int mode) {
    return new XSharedPreferences(packageName, prefFileName);
  }

  /** @deprecated Use {@link XSharedPreferences} instead. */
  @Deprecated
  public static SharedPreferences getDefaultSharedPreferencesForPackage(String packageName) {
    return new XSharedPreferences(packageName);
  }

  /** @deprecated Use {@link XSharedPreferences#reload} instead. */
  @Deprecated
  public static void reloadSharedPreferencesIfNeeded(SharedPreferences pref) {
    if (pref instanceof XSharedPreferences) {
      ((XSharedPreferences) pref).reload();
    }
  }
}

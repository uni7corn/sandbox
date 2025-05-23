package com.hello.sandbox.fake.service;

import android.app.ActivityManager;
import black.android.app.BRActivityTaskManager;
import black.android.app.BRIActivityTaskManagerStub;
import black.android.os.BRServiceManager;
import black.android.util.BRSingleton;
import java.lang.reflect.Method;
import com.hello.sandbox.fake.hook.BinderInvocationStub;
import com.hello.sandbox.fake.hook.MethodHook;
import com.hello.sandbox.fake.hook.ProxyMethod;
import com.hello.sandbox.fake.hook.ScanClass;
import com.hello.sandbox.utils.compat.TaskDescriptionCompat;

/** Created by Milk on 3/31/21. * ∧＿∧ (`･ω･∥ 丶　つ０ しーＪ 此处无Bug */
@ScanClass(ActivityManagerCommonProxy.class)
public class IActivityTaskManagerProxy extends BinderInvocationStub {
  public static final String TAG = "ActivityTaskManager";

  public IActivityTaskManagerProxy() {
    super(BRServiceManager.get().getService("activity_task"));
  }

  @Override
  protected Object getWho() {
    return BRIActivityTaskManagerStub.get()
        .asInterface(BRServiceManager.get().getService("activity_task"));
  }

  @Override
  protected void inject(Object baseInvocation, Object proxyInvocation) {
    replaceSystemService("activity_task");
    BRActivityTaskManager.get().getService();
    Object o = BRActivityTaskManager.get().IActivityTaskManagerSingleton();
    BRSingleton.get(o)._set_mInstance(BRIActivityTaskManagerStub.get().asInterface(this));
  }

  @Override
  public boolean isBadEnv() {
    return false;
  }

  // for >= Android 10 && < Android 12
  @ProxyMethod("setTaskDescription")
  public static class SetTaskDescription extends MethodHook {
    @Override
    protected Object hook(Object who, Method method, Object[] args) throws Throwable {
      ActivityManager.TaskDescription td = (ActivityManager.TaskDescription) args[1];
      args[1] = TaskDescriptionCompat.fix(td);
      return method.invoke(who, args);
    }
  }
}

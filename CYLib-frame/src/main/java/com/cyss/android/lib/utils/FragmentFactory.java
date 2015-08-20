package com.cyss.android.lib.utils;

import com.cyss.android.lib.CYFragment;

/**
 * Created by cyjss on 2015/8/20.
 */
public class FragmentFactory {

    public static CYFragment createFragment(Class clazz) {
        try {
            return (CYFragment) clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}

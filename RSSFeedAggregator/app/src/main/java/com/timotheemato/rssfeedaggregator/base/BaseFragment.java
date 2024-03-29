package com.timotheemato.rssfeedaggregator.base;

import android.support.v4.app.Fragment;

/**
 * Created by tmato on 1/21/17.
 */

public abstract class BaseFragment extends Fragment implements Lifecycle.View {

    protected abstract Lifecycle.ViewModel getViewModel();

    @Override
    public void onStart() {

        super.onStart();
        getViewModel().onViewAttached(this);
    }

    @Override
    public void onStop() {

        super.onStop();
        getViewModel().onViewDetached();
    }
}

package com.nobody.spoon.ui._1_main.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.nobody.spoon.R;
import com.nobody.spoon.app.Constants;
import com.nobody.spoon.base.RootFragment;
import com.nobody.spoon.base.contract.GoldContract;
import com.nobody.spoon.module.bean.GoldListBean;
import com.nobody.spoon.presenter.GoldPresenter;
import com.nobody.spoon.ui._1_main.adapter.GoldListAdapter;
import com.nobody.spoon.widget.GoldItemDecoration;
import com.nobody.spoon.widget.TouchSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Robin on 2018/3/30.
 */

public class GoldPagerFragment extends RootFragment<GoldPresenter> implements GoldContract.View {

    @BindView(R.id.view_main)
    RecyclerView rvGoldList;
    @BindView(R.id.swipe_refresh)
    TouchSwipeRefreshLayout swipeRefresh;

    @Override
    protected void initInject() {
        getFragmentComponent().inject(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_gold_page;
    }


    private String mType;
    private GoldItemDecoration mDecoration;
    private boolean isLoadingMore = false;
    private GoldListAdapter mAdapter;

    @Override
    protected void initEventAndData() {
        super.initEventAndData();
        mType = getArguments().getString(Constants.IT_GOLD_TYPE);
        mDecoration = new GoldItemDecoration();
        mAdapter = new GoldListAdapter(mContext, new ArrayList<GoldListBean>(), getArguments().getString(Constants.IT_GOLD_TYPE_STR));
        rvGoldList.setLayoutManager(new LinearLayoutManager(mContext));
        rvGoldList.setAdapter(mAdapter);
        rvGoldList.addItemDecoration(mDecoration);
        rvGoldList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) rvGoldList.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = rvGoldList.getLayoutManager().getItemCount();
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (!isLoadingMore) {
                        isLoadingMore = true;
                        presenter.getMoreGoldData();
                    }
                }
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mAdapter.getHotFlag()) {
                    rvGoldList.addItemDecoration(mDecoration);
                }
                mAdapter.setHotFlag(true);
                presenter.getGoldData(mType);
            }
        });
        mAdapter.setOnHotCloseListener(new GoldListAdapter.OnHotCloseListener() {
            @Override
            public void onClose() {
                rvGoldList.removeItemDecoration(mDecoration);
            }
        });
        stateLoading();
        presenter.getGoldData(mType);
    }

    @Override
    public void showContent(List<GoldListBean> goldListBean) {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
        stateMain();
        mAdapter.updateData(goldListBean);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMoreContent(List<GoldListBean> goldMoreListBean, int start, int end) {
        mAdapter.updateData(goldMoreListBean);
        mAdapter.notifyItemRangeInserted(start, end);
        isLoadingMore = false;
    }

    @Override
    public void stateError() {
        super.stateError();
        if(swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

}

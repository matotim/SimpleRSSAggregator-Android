package com.timotheemato.rssfeedaggregator.ui.feed;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timotheemato.rssfeedaggregator.R;
import com.timotheemato.rssfeedaggregator.base.BaseFragment;
import com.timotheemato.rssfeedaggregator.base.EndlessRecyclerViewScrollListener;
import com.timotheemato.rssfeedaggregator.base.Lifecycle;
import com.timotheemato.rssfeedaggregator.data.SharedPrefManager;
import com.timotheemato.rssfeedaggregator.network.RequestManager;
import com.timotheemato.rssfeedaggregator.network.models.Post;
import com.timotheemato.rssfeedaggregator.ui.adapters.PostAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedFragment extends BaseFragment implements FeedContract.View {

    private static String KEY_ID = "KEY_ID";
    private static String KEY_TITLE = "KEY_TITLE";

    @BindView(R.id.feed_title)
    TextView feedTitleTextView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;
    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;

    private FeedViewModel feedViewModel;
    private int feedId;
    private String feedTitle;

    private List<Post> postList;
    private EndlessRecyclerViewScrollListener scrollListener;
    private PostAdapter adapter;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance(int id, String title) {
        FeedFragment fragment = new FeedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ID, id);
        bundle.putString(KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            feedId = bundle.getInt(KEY_ID);
            feedTitle = bundle.getString(KEY_TITLE);
        }

        RequestManager requestManager =
                RequestManager.getInstance(getActivity().getApplicationContext());
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(getActivity().getApplicationContext());

        feedViewModel = new FeedViewModel(requestManager, sharedPrefManager);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (postList.size() == 0) {
            startLoading();
            feedViewModel.getPosts(feedId, 10, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        ButterKnife.bind(this, rootView);

        feedTitleTextView.setText(feedTitle);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();

        adapter = new PostAdapter(getContext());
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("FeedFragment", "Loading : page " + page + ", offset : " + totalItemsCount);
                feedViewModel.getPosts(feedId, 10, totalItemsCount);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);

        return rootView;
    }

    @Override
    protected Lifecycle.ViewModel getViewModel() {
        return feedViewModel;
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void stopLoading() {
        contentLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
    }

    public void startLoading() {
        contentLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError() {
        contentLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showContent(List<Post> postList) {
        stopLoading();
        if (postList.size() == 0 && adapter.getItemCount() == 0) {
            showError();
        } else if (postList.size() == 0 && adapter.getItemCount() != 0) {
            showMessage("No more posts");
            adapter.stopEndless();
            contentLayout.setVisibility(View.VISIBLE);
        } else {
            adapter.addPostToList(postList);
            contentLayout.setVisibility(View.VISIBLE);
        }
    }
}

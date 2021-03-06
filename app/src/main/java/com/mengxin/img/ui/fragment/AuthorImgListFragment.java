package com.mengxin.img.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.mengxin.img.R;
import com.mengxin.img.data.dto.Img;
import com.mengxin.img.net.HttpMethods;
import com.mengxin.img.ui.activity.AuthorActivity;
import com.mengxin.img.ui.adapter.IllustrationsAdapter;
import com.mengxin.img.utils.RxSchedulers;
import com.mengxin.img.utils.ToastUtils;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 图片显示List
 */
public class AuthorImgListFragment extends Fragment{

    private Long authorId;

    private ImageView img_list_back;
    private Toolbar toolbar;
    private SwipeRefreshLayout srl_refresh;
    private RecyclerView rec_mz;
    private CompositeDisposable mSubscriptions;
    private IllustrationsAdapter mAdapter;
    private int CurPage = 0;
    private ArrayList<Img> mData;
    private final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    public static AuthorImgListFragment newInstance(){
        return new AuthorImgListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.author_img_list, container, false);
        Slidr.attach(getActivity());
        srl_refresh = view.findViewById(R.id.srl_refresh);
        rec_mz = view.findViewById(R.id.rec_mz);
        img_list_back = view.findViewById(R.id.img_list_back);
        img_list_back.setOnClickListener(v -> {
            Intent intent  = new Intent(getActivity(), AuthorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("authorId",authorId);
            startActivity(intent);
        });
        srl_refresh.setOnRefreshListener(() -> {
            CurPage = 0;
            fetchAuthorImg(true);
        });
        final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        rec_mz.setLayoutManager(layoutManager);
        rec_mz.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {//加载更多
                    if (layoutManager.getItemCount() - recyclerView.getChildCount() <= layoutManager.findFirstVisibleItemPosition()) {
                        ++CurPage;
                        fetchAuthorImg(false);
                    }
                }
            }
        });
        toolbar = view.findViewById(R.id.toolbar0);
        toolbar.setNavigationOnClickListener(v -> {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            AuthorFragment fragment = AuthorFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putLong("authorId",authorId);
            fragment.setArguments(bundle);
            manager.beginTransaction().replace(R.id.author_content_container,fragment).commit();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSubscriptions = new CompositeDisposable();
        mData = new ArrayList<Img>();
        mAdapter = new IllustrationsAdapter(getActivity(), mData);
        rec_mz.setAdapter(mAdapter);
        srl_refresh.setRefreshing(true);
        Bundle bundle = getArguments();
        authorId = bundle.getLong("authorId",0);
        fetchAuthorImg(true);
    }
    /* 拉取作者所有作品 */
    private void fetchAuthorImg(boolean isRefresh){
        HttpMethods.getInstance().getAuthorImg(new Observer<ArrayList<Img>>() {
            private Disposable d;
            @Override
            public void onSubscribe(Disposable d) {
                this.d = d;
                mSubscriptions.add(d);
            }

            @Override
            public void onNext(ArrayList<Img> imgs) {
                if (imgs.isEmpty()){
                    d.dispose();
                    ToastUtils.shortToast("没有更多图片了");
                    srl_refresh.setRefreshing(false);
                }
                if (isRefresh){
                    mAdapter.addAll(imgs);
                } else {
                    mAdapter.loadMore(imgs);
                }
            }

            @Override
            public void onError(Throwable e) {
                d.dispose();
            }

            @Override
            public void onComplete() {
                srl_refresh.setRefreshing(false);
            }
        },authorId,CurPage*20);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.clear();
    }

}

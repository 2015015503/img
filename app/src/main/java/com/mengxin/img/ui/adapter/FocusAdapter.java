package com.mengxin.img.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mengxin.img.R;
import com.mengxin.img.data.dto.Author;
import com.mengxin.img.data.dto.Img;
import com.mengxin.img.net.HttpMethods;
import com.mengxin.img.ui.activity.AuthorActivity;
import com.mengxin.img.ui.activity.PictureDetailActivity;
import com.mengxin.img.utils.NetworkUtils;
import com.mengxin.img.utils.ToastUtils;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class FocusAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<Author> authors;
    private long meId;

    public FocusAdapter(Context context, ArrayList<Author> authors) {
        this.context = context;
        this.authors = authors;
        meId = NetworkUtils.isLogin(context);
    }

    @Override
    public int getCount() {
        return authors.size();
    }

    @Override
    public Object getItem(int position) {
        return authors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Author author = authors.get(position);
        long authorId = author.getId();
        ArrayList<Img> imgList = author.getImgList();
        int num = imgList.size();
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.focus_item,null);
        }
        Button focus = convertView.findViewById(R.id.btn_state_focus);
        if (authorId != meId){
            HttpMethods.getInstance().isFocus(new Observer<Boolean>() {
                boolean isFocus;
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Boolean aBoolean) {
                    isFocus = aBoolean;
                }

                @Override
                public void onError(Throwable e) {

                }

                @SuppressLint("ResourceAsColor")
                @Override
                public void onComplete() {
                    Resources resources = context.getResources();
                    if (isFocus){
                        focus.setText("正在关注");
                        focus.setBackground(resources.getDrawable(R.color.colorPrimaryDark));
                        focus.setTextColor(resources.getColor(R.color.white));
                    } else {
                        focus.setText("关注");
                        focus.setBackground(resources.getDrawable(R.color.white));
                        focus.setTextColor(resources.getColor(R.color.colorPrimaryDark));
                    }
                    focus.setOnClickListener(v -> {
                        if (isFocus){
                            HttpMethods.getInstance().unFocus(new Observer<Boolean>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Boolean aBoolean) {
                                    if (aBoolean == true){
                                        isFocus = false;
                                        focus.setText("关注");
                                        focus.setBackground(resources.getDrawable(R.color.white));
                                        focus.setTextColor(resources.getColor(R.color.colorPrimaryDark));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {

                                }
                            },authorId,meId);
                        } else {
                            if (meId == 0L){
                                ToastUtils.shortToast("尚未登录");
                            } else {
                                HttpMethods.getInstance().focus(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        if (aBoolean){
                                            isFocus = true;
                                            focus.setText("正在关注");
                                            focus.setBackground(resources.getDrawable(R.color.colorPrimaryDark));
                                            focus.setTextColor(resources.getColor(R.color.white));
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                },authorId,meId);
                            }
                        }
                    });
                }
            },authorId,meId);
        } else {
            focus.setVisibility(View.GONE);
        }
        RoundedImageView header = convertView.findViewById(R.id.iv_header_focus);
        TextView authorName = convertView.findViewById(R.id.tv_author_name);
        ImageView img1 = convertView.findViewById(R.id.iv_img1);
        ImageView img2 = convertView.findViewById(R.id.iv_img2);
        ImageView img3 = convertView.findViewById(R.id.iv_img3);
        ArrayList<ImageView> imageViews = new ArrayList<>(3);
        imageViews.add(img1);
        imageViews.add(img2);
        imageViews.add(img3);
        for (int i = 0; i < num; i++){
            ImageView view0 = imageViews.get(i);
            Img img = imgList.get(i);
            view0.setClickable(true);
            Glide.with(context)
                    .load(img.getSrc())
                    .apply(new RequestOptions()
                            .centerCrop())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (view0.getScaleType() != ImageView.ScaleType.FIT_XY) {
                                view0.setScaleType(ImageView.ScaleType.FIT_XY);
                            }
                            ViewGroup.LayoutParams params = view0.getLayoutParams();
                            int viewWidth = view0.getWidth();
                            float scale = (float) viewWidth / (float) resource.getIntrinsicWidth();
                            int viewHeight = Math.round(resource.getIntrinsicHeight() * scale);
                            params.height = viewHeight;
                            view0.setLayoutParams(params);
                            return false;
                        }
                    })
                    .into(view0);
            view0.setOnClickListener(v -> {
                Intent intent = new Intent(context, PictureDetailActivity.class);
                intent.putExtra("img_id",img.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            });
        }
        Glide.with(context).asBitmap().load(author.getHeadImg()).into(header);
        header.setOnClickListener(v -> {
            Intent intent = new Intent(context, AuthorActivity.class);
            intent.putExtra("authorId",authorId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        });
        authorName.setText(author.getName());
        return convertView;
    }


}

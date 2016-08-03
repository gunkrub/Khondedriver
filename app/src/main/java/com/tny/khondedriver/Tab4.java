package com.tny.khondedriver;

/**
 * Created by Thitiphat on 9/17/2015.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab4 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_4,container,false);
        final View v2 = v;

        Picasso.with(getContext()).load(getString(R.string.website_url) + "get_document_pic.php?type_id=1&tel=" + MainActivity.tel).transform(new CircleTransform()).into((ImageView) v.findViewById(R.id.profile_icon),
                new Callback() {
                    @Override
                    public void onSuccess() {
                        v2.findViewById(R.id.profile_icon_loading).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        return v;
    }
}
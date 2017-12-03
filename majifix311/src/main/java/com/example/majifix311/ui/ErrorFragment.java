package com.example.majifix311.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.majifix311.R;


public class ErrorFragment extends Fragment {

    public static final String ERROR_MSG = "ERROR_MSG";
    public static final String ERROR_ICN = "ERROR_ICN";

    private ImageView ivErrorIcon;
    private TextView tvErrorText;
    private ImageView ivReload;
    private TextView tvReload;

    private OnReloadClickListener mClickListener;

    // This allows the user to customize the error message and icon
    public static ErrorFragment getInstance(@NonNull String msg, int icn) {
        Bundle args = new Bundle();
        args.putString(ErrorFragment.ERROR_MSG, msg);
        args.putInt(ErrorFragment.ERROR_ICN, icn);

        ErrorFragment fragment = new ErrorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnReloadClickListener) {
            this.mClickListener = (OnReloadClickListener) context;
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_error, viewGroup, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // bind data to the components
        Bundle bundle = getArguments();
        String errMsg;
        int imageRes;
        if (bundle != null) {
            errMsg = bundle.getString(ERROR_MSG, getString(R.string.error_server));
            imageRes = bundle.getInt(ERROR_ICN, R.drawable.ic_cloud_off_48x48);
        } else {
            errMsg = getString(R.string.error_server);
            imageRes = R.drawable.ic_cloud_off_48x48;
        }

        ivErrorIcon = (ImageView) view.findViewById(R.id.iv_errorIcon);
        tvErrorText = (TextView) view.findViewById(R.id.tv_errorMsg);
        ivReload = (ImageView) view.findViewById(R.id.iv_reload);
        tvReload = (TextView) view.findViewById(R.id.tv_reload);

        ivErrorIcon.setImageResource(imageRes);
        tvErrorText.setText(errMsg);

        // when the refresh button is hit
        if (mClickListener == null) {
            ivReload.setVisibility(View.GONE);
            tvReload.setVisibility(View.GONE);
        } else {
            ivReload.setOnClickListener(onReloadClicked);
            tvReload.setOnClickListener(onReloadClicked);
        }
    }

    private View.OnClickListener onReloadClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onReloadClicked();
            }
        }
    };

    /* Bridge communication between this fragment and the attached activity */
    public interface OnReloadClickListener {

        /* callback to execute when the click action is performed */
        void onReloadClicked();
    }
}

package com.easyhome.sample.share;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.easyhome.common.modules.share.logic.IShareOption;
import com.easyhome.common.modules.share.logic.ShareManager;
import com.easyhome.common.modules.share.model.IShareObject;
import com.easyhome.common.uikit.app.BaseFragment;
import com.easyhome.common.utils.TextUtil;
import com.easyhome.sample.R;
import com.easyhome.test.ShareTester;

/**
 * 分享展示UI
 */
public class ShareActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        ShareManager.getInstance().initOptions(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ShareFragment())
                    .commit();
        } else {
            ShareManager.getInstance().onHandleNewIntent(this, getIntent());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareManager.getInstance().onHandleActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        ShareManager.getInstance().onHandleNewIntent(this, intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ShareFragment extends BaseFragment implements IShareOption.IShareListener {

        public static BaseFragment newInstance() {
            ShareFragment fragment = new ShareFragment();
            fragment.setHasOptionsMenu(true);
            return fragment;
        }

        private RadioGroup mRadioGroup;

        @Override
        public CharSequence getTitle() {
            return getString(R.string.title_section_share);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.share, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            IShareObject shareObject = null;
            switch (id) {
                case R.id.action_weiblog:
                    shareObject = prepareShareObject(ShareManager.OPTION_WEIBLOG);
                    ShareManager.getInstance().share(getActivity(), ShareManager.OPTION_WEIBLOG, shareObject, this);
                    break;
                case R.id.action_weichat:
                    shareObject = prepareShareObject(ShareManager.OPTION_WEICHAT);
                    ShareManager.getInstance().share(getActivity(), ShareManager.OPTION_WEICHAT, shareObject, this);
                    break;
                case R.id.action_weichat_friends:
                    shareObject = prepareShareObject(ShareManager.OPTION_WEICHAT_FRIENDS);
                    ShareManager.getInstance().share(getActivity(), ShareManager.OPTION_WEICHAT_FRIENDS, shareObject, this);
                    break;
                case R.id.action_qqzone:
                    shareObject = prepareShareObject(ShareManager.OPTION_QQZONE);
                    ShareManager.getInstance().share(getActivity(), ShareManager.OPTION_QQZONE, shareObject, this);
                    break;
                case R.id.action_qqweibo:
                    shareObject = prepareShareObject(ShareManager.OPTION_QQWEIBO);
                    ShareManager.getInstance().share(getActivity(), ShareManager.OPTION_QQWEIBO, shareObject, this);
                    break;
                case R.id.action_qqfriends:
                    shareObject = prepareShareObject(ShareManager.OPTION_QQFIRENTS);
                    ShareManager.getInstance().share(getActivity(), ShareManager.OPTION_QQFIRENTS, shareObject, this);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
            return true;
        }

        /**
         * 测试数据
         * @param option
         * @return
         */
        private IShareObject prepareShareObject(int option) {
            IShareObject shareObject = null;
            switch (mRadioGroup.getCheckedRadioButtonId()) {
                case R.id.radio_share_txt:
                    shareObject = ShareTester.get(getActivity(), option).getShareText();
                    break;
                case R.id.radio_share_audio:
                    shareObject = ShareTester.get(getActivity(), option).getShareAudio();
                    break;
                case R.id.radio_share_image:
                    shareObject = ShareTester.get(getActivity(), option).getShareImage();
                    break;
                case R.id.radio_share_video:
                    shareObject = ShareTester.get(getActivity(), option).getShareVidio();
                    break;
                case R.id.radio_share_url:
                    shareObject = ShareTester.get(getActivity(), option).getShareWebpage();
                    break;
            }
            return shareObject;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_share, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        }

        /**
         * 分享结果反馈
         *
         * @param success
         * @param message
         */
        @Override
        public void onResponceShare(IShareOption option, boolean success, String message) {
			if (!TextUtil.isEmpty(message)) {
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
			}
        }

		@Override
		public void onResponceAuth(IShareOption option, boolean success, String message) {

		}
	}

}

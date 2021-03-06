package com.easyhome.common.modules.share.logic;

import android.content.Context;
import android.content.Intent;

import com.easyhome.common.modules.share.ShareConfiguration;
import com.easyhome.common.modules.share.model.IShareObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 分享管理
 */
public class ShareManager {

    public static final int OPTION_WEIBLOG = 0; /*微博*/
    public static final int OPTION_WEICHAT = 1; /*微信好友*/
    public static final int OPTION_WEICHAT_FRIENDS = 2; /*微信朋友圈*/
    public static final int OPTION_QQZONE = 3; /*QQ空间*/
    public static final int OPTION_QQFIRENTS = 4; /*QQ好友*/
    public static final int OPTION_QQWEIBO = 5; /*QQ微博*/
    public static final int OPTION_RENREN = 6; /*人人网*/

    private static ShareManager INSTANCE;
    private ShareCreator mShareCreator;

    private Map<Integer, BaseOption> mCreatedOptions = new HashMap<Integer, BaseOption>();

    private ShareManager() {
        mShareCreator = new ShareCreator();
    }

    public static ShareManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShareManager();
        }
        return INSTANCE;
    }

	/**
	 * 检测是否支持该option分享
	 * @param context
	 * @param option
	 * @return
	 */
	public boolean isSupport(Context context, int option) {
		BaseOption shareOption = mShareCreator.createShareOption(context, option);
		if (shareOption != null) {
			return shareOption.checkSupport();
		}
		return false;
	}

	/**
	 * 是否安装软件
	 * @param context
	 * @param option
	 * @return
	 */
	public boolean isInstalled(Context context, int option) {
		BaseOption shareOption = mShareCreator.createShareOption(context, option);
		if (shareOption != null) {
			return shareOption.isInstalledApp();
		}
		return false;
	}

	/**
	 * 是否开启消息通知
	 * @param enable
	 */
	public void enableShowNotify(boolean enable) {
		ShareConfiguration.ENABLE_SHOW_NOTIFY = enable;
	}

    /**
     * 进行登录
     *
     * @param context
     * @param option
     * @param listener
     */
    public void login(Context context, int option, IShareOption.IShareListener listener) {
        BaseOption shareOption = mShareCreator.createShareOption(context, option, listener);
        if (shareOption != null) {
            shareOption.doLogin();
        }
    }

    /**
     * 进行授权
     *
     * @param context
     * @param option
     * @param listener
     */
    public void auth(Context context, int option, IShareOption.IShareListener listener) {
        BaseOption shareOption = mShareCreator.createShareOption(context, option, listener);
        if (shareOption != null) {
            shareOption.doAuth();
        }
    }

    /**
     * 进行分享
     *
     * @param option      {@link #OPTION_QQZONE#OPTION_WEIBLOG#OPTION_WEICHAT#OPTION_WEICHAT_FRIENDS}
     * @param shareObject
     * @param listener
     */
    public void share(Context context, int option, IShareObject shareObject, IShareOption.IShareListener listener) {
        BaseOption shareOption = mShareCreator.createShareOption(context, option, shareObject, listener);
        if (shareOption != null) {
            shareOption.doShare();
        }
    }

	public void clearAllAuth() {
		for (BaseOption option : mCreatedOptions.values()) {
			option.onCancelAuth();
		}
	}

    /**
     * Handle Activity's onActivityResult() method.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onHandleActivityResult(Context context, int requestCode, int resultCode, Intent data) {
        synchronized (mShareCreator) {
            for (BaseOption shareOption : mCreatedOptions.values()) {
                if (data == null) {
                    data = new Intent(shareOption.getCurrentAction());
                }
                data.putExtra(BaseOption.EXTREA_REQUEST_CODE, requestCode);
                data.putExtra(BaseOption.EXTREA_RESULT_CODE, resultCode);
                data.putExtra(BaseOption.EXTREA_TYPE, BaseOption.TYPE_ACTIVITY_RESULT);
                shareOption.onEvent(context, data);
            }
        }
    }

    /**
     * Handle Activity's onNewIntent(intent) method.
     *
     * @param intent
     */
    public boolean onHandleNewIntent(Context context, Intent intent) {
        synchronized (mShareCreator) {
            boolean handled = false;
            for (BaseOption shareOption : mCreatedOptions.values()) {
                if (intent == null) {
                    intent = new Intent(shareOption.getCurrentAction());
                }
                intent.putExtra(BaseOption.EXTREA_TYPE, BaseOption.TYPE_NEW_INTENT);
                if (shareOption.onEvent(context, intent)) {
                    handled = true;
                }
            }
            return handled;
        }
    }

    public void initOptions(Context context) {
        mShareCreator.createShareOption(context, OPTION_WEIBLOG);
        mShareCreator.createShareOption(context, OPTION_WEICHAT);
        mShareCreator.createShareOption(context, OPTION_WEICHAT_FRIENDS);
        mShareCreator.createShareOption(context, OPTION_QQZONE);
        mShareCreator.createShareOption(context, OPTION_QQFIRENTS);
        mShareCreator.createShareOption(context, OPTION_QQWEIBO);
        mShareCreator.createShareOption(context, OPTION_RENREN);
    }

    public IShareOption getOption(int option) {
        return mCreatedOptions.get(option);
    }

    /**
     * 分享项工厂
     */
    public class ShareCreator {

        public synchronized BaseOption createShareOption(Context context, int option) {
            BaseOption shareOption = mCreatedOptions.get(option);
            switch (option) {
                case OPTION_WEIBLOG:
                    if (!ShareConfiguration.ENABLE_WEIBLOG) {
                        return null;
                    }
                    if (shareOption == null) {
                        shareOption = new WeiBlog(context, null);
                        mCreatedOptions.put(option, shareOption);
                    }
                    break;
                case OPTION_WEICHAT:
                    if (!ShareConfiguration.ENABLE_WEICHAT) {
                        return null;
                    }
                    if (shareOption == null) {
                        shareOption = new WeiChat(context, null);
                        mCreatedOptions.put(option, shareOption);
                    }
                    break;
                case OPTION_WEICHAT_FRIENDS:
                    if (!ShareConfiguration.ENABLE_WEICHAT) {
                        return null;
                    }
                    if (shareOption == null) {
                        shareOption = new WeiChat(context, null);
                        mCreatedOptions.put(option, shareOption);
                    }
                    ((WeiChat) shareOption).setEnableTimeline(ShareConfiguration.ENABLE_WEICHAT_SHARE_FIRENDS);
                    break;
                case OPTION_QQZONE:
                    if (!ShareConfiguration.ENABLE_QQZONE) {
                        return null;
                    }
                    if (shareOption == null
							|| (shareOption instanceof QQzoneWeb && shareOption.isSupportSSO())) {
						shareOption = new QQZone(context);
						mCreatedOptions.put(option, shareOption);
                    } else if (shareOption instanceof QQZone && !shareOption.isSupportSSO()) {
						shareOption = new QQzoneWeb(context);
						mCreatedOptions.put(option, shareOption);
					}
					break;
                case OPTION_QQFIRENTS:
                    if (!ShareConfiguration.ENABLE_QQFRIENDS) {
                        return null;
                    }
                    if (shareOption == null) {
                        shareOption = new QQFriends(context);
                        mCreatedOptions.put(option, shareOption);
                    }
                    break;
                case OPTION_QQWEIBO:
                    if (!ShareConfiguration.ENABLE_QQWEIBO) {
                        return null;
                    }
                    if (shareOption == null) {
                        shareOption = new QQWeibo(context);
                        mCreatedOptions.put(option, shareOption);
                    }
                    break;
                case OPTION_RENREN:
                    if (!ShareConfiguration.ENABLE_RENREN) {
                        return null;
                    }
                    if (shareOption == null) {
                        shareOption = new RenRen(context);
                        mCreatedOptions.put(option, shareOption);
                    }
                    break;
            }
            return shareOption;
        }

        public synchronized BaseOption createShareOption(Context context, int option, IShareObject shareObject, IShareOption.IShareListener listener) {
            BaseOption shareOption = createShareOption(context, option);
            if (shareOption != null) {
                shareOption.setContext(context);
                shareOption.setShareObject(shareObject);
                shareOption.setShareListener(listener);
            }
            return shareOption;
        }

        public synchronized BaseOption createShareOption(Context context, int option, IShareOption.IShareListener listener) {
            BaseOption shareOption = createShareOption(context, option);
            if (shareOption != null) {
                shareOption.setContext(context);
                shareOption.setShareListener(listener);
            }
            return shareOption;
        }
    }

}

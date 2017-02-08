package com.popdq.app.values;

/**
 * Created by Dang Luu on 07/07/2016.
 */
public class Values {
    public static final String PROJECT_TOKEN_MIXPANEL = "d08f91edad797b96136b337d36e2462b";
    public static final String TRACK_SEARCH_KEYWORD = "search_keyword";
    public static final String TRACK_CATEGORY= "category";
    public static final String TRACK_VIEW_ALL= "click_view_all";
    public static final String TRACK_POP_THIS_QUESTION= "click_pop_this_question";

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String CURRENT_TOKEN = "current_token";
    public static final String TOKEN = "token";
    public static final String fbToken = "fbToken";
    public static final String question_id = "question_id";
    public static final String code = "code";
    public static final String access_token = "access_token";
    public static final String id_token = "id_token";
    public static final String client_type = "client_type";
    public static final String username = "username";
    public static final String name = "name";
    public static final String firstname = "firstname";
    public static final String lastname = "lastname";
    public static final String avatar = "avatar";
    public static final String status_anonymous = "status_anonymous";
    public static final String experts_id = "experts_id";
    public static final String config_charge = "config_charge";
    public static final String professional_field = "professional_field";
    public static final String experience = "experience";
    public static final String address = "address";
    public static final String isViewed = "isViewed";

    public static final String search = "search";


    public static final String category = "category";
    public static final String newExpertCategory = "newExpertCategory";
    public static final String category_id = "category_id";
    public static final String category_name = "category_name";
    public static final String user_id_answer = "user_id_answer";
    public static final String title = "title";
    public static final String description = "description";
    public static final String attachments = "attachments";
    public static final String attachments_arr = "attachments[]";
    public static final String method = "method";
    public static final String language_written = "language_written";
    public static final String language_spoken = "language_spoken";
    public static final String free_preview = "free_preview";
    public static final String type = "type";


    public static final String keyword = "keyword";
    public static final String tag = "tag";
    public static final String limit = "limit";
    public static final String offset = "offset";
    public static final String content = "content";
    public static final String rating = "rating";
    public static final String answer_id = "answer_id";
    public static final String answer_content = "answer_content";
    public static final String newInterest = "newInterest";
    public static final String user = "user";
    public static final String user_id = "user_id";
    public static final String favorite = "favorite";
    public static final String clientId = "clientId";
    public static final String bundleId = "bundleId";
    public static final String created_timestamp = "created_timestamp";


    public static final String text_credit = "text_credit";
    public static final String voice_credit = "voice_credit";
    public static final String video_credit = "video_credit";


//        public static final String BASE_URL = "http://54.169.105.228/giangdv/popdq/public_html";
//    public static final String BASE_URL_AVATAR = "http://54.169.105.228/giangdv/popdq/";

//  public static final String BASE_URL = "http://api.popdq.com/api";

//    public static final String BASE_URL_AVATAR = "http://api.popdq.com/";


//    public static final String BASE_URL = "http://35.164.146.96/api";
//    public static final String BASE_URL_AVATAR = "http://35.164.146.96/";

    public static final String BASE_URL = "http://apiv2.popdq.com/api";
    public static final String BASE_URL_AVATAR = "http://apiv2.popdq.com/";

//    public static final String BASE_URL = "http://52.221.10.148:8000/api";
//    public static final String BASE_URL_AVATAR = "http://52.221.10.148:8000/";

    public static final String URL_USER_SEARCH = BASE_URL + "/user/searchAll";
    public static final String URL_USER_MY_PROFILE = BASE_URL + "/user/myProfile";
    public static final String URL_USER_LOGIN_WITH_GOOGLE = BASE_URL + "/user/loginWithGoogle";
    public static final String URL_USER_LOGIN_WITH_FB = BASE_URL + "/user/loginWithFacebook";
    public static final String URL_USER_UPDATE_INFO_USER = BASE_URL + "/user/update";

//    public static final String URL_USER_LOGIN_WITH_FB = "http://52.221.10.148/api/user/loginWithFacebook";
//    public static final String URL_USER_LOGIN_WITH_GOOGLE = "http://52.221.10.148/api/user/loginWithGoogle";
//    public static final String URL_USER_UPDATE_INFO_USER = "http://52.221.10.148//user/update";



    public static final String URL_USER_LOGIN_WITH_EMAIL = BASE_URL + "/user/loginWithEmail";
    public static final String URL_USER_SIGNUP_WITH_EMAIL = BASE_URL + "/user/signup";
    public static final String URL_USER_FORGOT_PASWORD = BASE_URL + "/user/forgotPassword";
    public static final String URL_USER_GET_LIST_INTEREST = BASE_URL + "/category/listInterest";
    public static final String URL_USER_UPDATE_LIST_INTEREST = BASE_URL + "/category/updateInterest";
    public static final String URL_USER_ACTIVATE = BASE_URL + "/user/activate";
    public static final String URL_USER_REGISTER_PUSH = BASE_URL + "/user/registerPush";
    public static final String URL_USER_UNREGISTER_PUSH = BASE_URL + "/user/unregisterPush";
    public static final String URL_USER_RESEND = BASE_URL + "/user/resendActivateCode";
    public static final String URL_USER_RESET_PASS = BASE_URL + "/user/resetPassword";
    public static final String URL_USER_UPDATE_CATEGORIES = BASE_URL + "/category/updateExpertCategory";
    public static final String URL_USER_GET_MY_PROFILE = BASE_URL + "/user/myProfile";
    public static final String URL_USER_GET_FRIEND_FACEBOOK = BASE_URL + "/user/searchFbFriend";
    public static final String URL_USER_CHANGE_PASSWORD = BASE_URL + "/user/changePassword";


    public static final String URL_QUESTION_SEARCH = BASE_URL + "/question/search";
    public static final String URL_QUESTION_MY_LIST = BASE_URL + "/question/myListQuestion";
    public static final String URL_QUESTION_GET_LIST = BASE_URL + "/question/search";
    public static final String URL_QUESTION_GET_DETAIL = BASE_URL + "/question/detail";
    public static final String URL_QUESTION_CREATE = BASE_URL + "/question/create";
    public static final String URL_QUESTION_REPORT = BASE_URL + "/question/report";
    public static final String URL_QUESTION_REJECT = BASE_URL + "/question/reject";
    public static final String URL_QUESTION_ANSWERED = BASE_URL + "/question/answeredQuestion";
    public static final String URL_QUESTION_MY_PREVIEW = BASE_URL + "/question/myPreview";
    public static final String URL_QUESTION_GET_VIEWER = BASE_URL + "/question/getViewers";

    public static final String URL_ANSWER_CREATE = BASE_URL + "/answer/create";
    public static final String URL_ANSWER_UPDATE = BASE_URL + "/answer/update";
    public static final String URL_ANSWER_LIST_ANSWER = BASE_URL + "/answer/listAnswer";
    public static final String URL_ANSWER_MY_LIST_ANSWER = BASE_URL + "/answer/myListAnswer";
    public static final String URL_ANSWER_RATEING = BASE_URL + "/answer/rating";
    public static final String URL_ANSWER_SEARCH = BASE_URL + "/answer/search";
    public static final String URL_ANSWER_REPORT = BASE_URL + "/answer/report";

    public static final String URL_FAROVITE_ADD = BASE_URL + "/favorite/add";
    public static final String URL_FAROVITE_REMOVE = BASE_URL + "/favorite/remove";
    public static final String URL_FAROVITE_SEARCH = BASE_URL + "/favorite/search";

    public static final String URL_EXPERT_SEARCH = BASE_URL + "/experts/search";
    public static final String URL_EXPERT_DETAIL = BASE_URL + "/experts/detail";
    public static final String URL_FILE_UPLOAD = BASE_URL + "/uploadFile/index";
    public static final String URL_CREDIT_CONVERT = BASE_URL + "/transaction/convertCredit";
    public static final String URL_CREDIT_VERIFY = BASE_URL + "/transaction/verify";
    public static final String URL_CREDIT_LIST_CREDIT = BASE_URL + "/transaction/listCredit";
    public static final String URL_TRANSACTION_MY_LIST = BASE_URL + "/transaction/myList";
    public static final String URL_TRANSACTION_WITHDRAW = BASE_URL + "/transaction/withDraw";

    public static final String URL_GET_NOTIFICATION = BASE_URL + "/notification/list";
    public static final String URL_PUSH_NOTIFICATION = BASE_URL + "/user/notificationRegister";
    public static final String URL_NOTIFICATION_READ = BASE_URL + "/notification/read";
    public static final String URL_NOTIFICATION_READ_ALL = BASE_URL + "/notification/readAll";
    public static final String URL_NOTIFICATION_GET_COUNT_UNREAD = BASE_URL + "/notification/countUnread";


    public static final String URL_USER_SEND_CODE_VERIFY = BASE_URL + "/user/sendVerifyCode";
    public static final String URL_USER_EMAIL_VERIFY = BASE_URL + "/user/verifyEmail";


    public static final String URL_ = "";

    public static final String URL_GET_LAST_VERSION = "http://apiv2.popdq.com/api/global/getAndroidAppVersion";


    //    public static final String[] list_credit_text = new String[]{"$FREE", "$0.99", "$4.99", "$9.99", "$24.99", "$49.99", "$99.99", "$249.99" , "Disable"};
//    public static final String[] list_credit_voice = new String[]{"$FREE", "$0.99", "$4.99", "$9.99", "$24.99", "$49.99", "$99.99", "$199.99", "$299.99"};
//    public static final String[] list_credit_video = new String[]{"$FREE", "$0.99", "$4.99", "$9.99", "$49.99", "$99.99", "$249.99", "$499.99"};
    public static final String image = "image";
    public static final int SIZE_AVATAR = 300;
    public static final String FEE_VIEW_ANSWER = "fee_view_answer";
    public static final String GET_CATEGORY = "get_category";
    public static final String WITHDRAW_AMOUNT = "withdraw_amount";
    public static final String LIST_CREDIT_CAN_BUY = "list_credit_can_buy";
    public static final String FROM_NOTIFICATION_BAR = "from_notification_bar";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String notificationId = "notificationId";
    public static final String MY_FAVORITE = "my_favorite";
    public static final String COUNT_NOTIFICATION = "count_notification";
    public static final String VERSION_UPDATE_NOT_SHOW = "version_update_not_show";
    public static final String KEY_STATUS_APP = "status_app";
    public static String index = "index";
    public static String my_user_id = "my_user_id";
    public static String category_respone = "category_respone";
    public static String total = "total";


    public static String LINK_FAQ = "http://www.popdq.com/faq.html";
    public static String LINK_HOW_WORK = "http://www.popdq.com/faq.html";
    //    public static String LINK_PRIVACY = "http://popmeaquestion.wixsite.com/popdq/privacy-policy";
//    public static String LINK_TERMS = "http://popmeaquestion.wixsite.com/popdq/terms-conditions";
//    public static String LINK_INTRO = "http://popmeaquestion.wixsite.com/popdq/about-popdq";
    public static String LINK_PRIVACY = "http://www.popdq.com/privacy-policy.html";
    public static String LINK_TERMS = "http://www.popdq.com/term-conditions.html";
    public static String LINK_INTRO = "http://www.popdq.com/about.html";
    public static String LINK_JOIN = "http://www.popdq.com/joinexpert.html";
    public static final String[] languages = {
            "English",
            "Bahasa",
            "Cantonese",
            "Mandarin"
    };


/*
    public static final String[] languages = {
            "AKAN",
            "AMHARIC",
            "ARABIC",
            "ASSAMESE",
            "AWADHI",
            "AZERBAIJANI",
            "BALOCHI",
            "BELARUSIAN",
            "BENGALI",
            "BHOJPURI",
            "BURMESE",
            "CEBUANO",
            "CHEWA",
            "CHHATTISGARHI",
            "CHITTAGONIAN",
            "CZECH",
            "DECCAN",
            "DHUNDHARI",
            "DUTCH",
            "EASTERN MIN",
            "ENGLISH",
            "FRENCH",
            "FULA",
            "GAN CHINESE",
            "GERMAN",
            "GREEK",
            "GUJARATI",
            "HAITIAN CREOLE",
            "HAKKA",
            "HARYANVI",

            "HAUSA",
            "HILIGAYNON",
            "HINDI",
            "HMONG",
            "HUNGGARIAN",
            "JGBO",
            "ILOCANO",
            "ITALIAN",
            "JAPANESE",
            "JAVANESE",

            "JIN",
            "KANNADA",
            "KAZAKH",
            "KHMER",
            "KINYARWANDA",
            "KIRUNDI",
            "KONKANI",
            "KOREAN",
            "KURDISH",
            "MADURESE",

            "MAGAHI",
            "MAITHILI",
            "MALAGASY",
            "MALAYALAM",
            "MALAYSIA/INDONESIAN",
            "MANDARIN",
            "MARATHI",
            "MARWARI",
            "MOSSI",
            "NEPALI",

            "NORTHERN MIN",
            "ODIA",
            "OROMO",
            "PASHTO",
            "PERSIAN",
            "POLISH",
            "PORTUGUESE",
            "PUNJABI",
            "QUECHUA",
            "ROMANIAN",

            "RUSSIAN",
            "SARAIKI",
            "SERBO-CROATIAN",
            "SHONA",
            "SINDHI",
            "SINHALESE",
            "SOMALI",
            "SOUTHERN MIN",
            "SPANISH",
            "SUNDANESE",

            "SWEDISH",
            "SYLHETI",
            "TAGALOG/FILIPINO",
            "TAMIL",
            "TELUGU",
            "THAI",
            "TURKISH",
            "TURKMEN",
            "UKRAINIAN",
            "URDU",

            "UYGHUR",
            "UZBEK",
            "VIETNAMESE",
            "WU",
            "XHOSA",
            "XIANG",
            "YORUBA",
            "YUE",
            "ZHUANG",
            "ZULU",
    };
*/

    public static final int PER_UPDATE_SEEKBAR = 200;
    public static String purchased = "purchased";
    public static String credit = "credit";
    public static String bank_name = "bank_name";
    public static String bank_account = "bank_acc";
    public static String your_name = "bank_account_name";
    public static String email = "email";
    public static String receipt_data = "receipt_data";

    public static final float FEE_VIEW_ANSWER_TEXT = 0.1f;
    public static final float FEE_VIEW_ANSWER_VOICE = 0.2f;
    public static final float FEE_VIEW_ANSWER_VIDEO = 0.3f;
    public static String token_push = "token_push";
    public static String status = "status";
    public static String password = "password";
    public static String newPassword = "newPassword";
    public static String verified = "verified";
    public static String nonverified = "nonverified";
    public static String find_user_category = "find_user_category";
    public static String expertCategory = "expertCategory";
    public static String message = "message";

    /*
    $r = 2;$message = 'token does not exist';
    $r = 4; $message = 'user not activated';
    $r = 9;$message = 'user does not exist';
    $r = 35; $message = 'token not equals user token';
     */
}

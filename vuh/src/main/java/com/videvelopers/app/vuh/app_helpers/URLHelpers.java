package com.videvelopers.app.vuh.app_helpers;

public class URLHelpers {

    //URL prefix
    private final static String DEFAULT_URL_PREFIX="https://api.recordingclub.in/";

    //gernal
    public final String getAppUpdate=DEFAULT_URL_PREFIX+"getAppUpdate.php";

    //Account links
    public final String account_login=DEFAULT_URL_PREFIX+"account/user_login.php";
    public final String account_get_info=DEFAULT_URL_PREFIX+"account/get_account_info.php";
    public final String setUserRole = DEFAULT_URL_PREFIX + "/account/setUserRoll.php";
    public final String changeUserRole=DEFAULT_URL_PREFIX+"account/setUserRoll.php";
    public final String get_all_users = DEFAULT_URL_PREFIX + "admin/get_all_users.php";
public final String get_all_subscribers = DEFAULT_URL_PREFIX + "admin/get_subscribed_users.php";
public final String getAllFreeUsers = DEFAULT_URL_PREFIX + "admin/get_all_free_users.php";
public final String deleteSubscription = DEFAULT_URL_PREFIX + "admin/delete_subscription.php";

    //Newspapers
    public final String newspaper_set_newspaper=DEFAULT_URL_PREFIX+"newspapers/set_newspaper.php";
    public final String newspapers_set_newspaper_daily_post=DEFAULT_URL_PREFIX+"newspapers/set_newspaper_daily_post.php";
    public final String newspaper_get_newspapers=DEFAULT_URL_PREFIX+"newspapers/get_newspapers.php";
    public final String newspaper_get_newspaper_daily_post=DEFAULT_URL_PREFIX+"newspapers/get_newspaper_daily_posts.php";
    public final String newspaper_get_newspaper_daily_post_info=DEFAULT_URL_PREFIX+"newspapers/get_newspaper_daily_post_info.php";
    public final String newspaper_get_newspaper_daily_post_by_newspaper_name=DEFAULT_URL_PREFIX+"newspapers/get_newspaper_daily_posts_by_newspaper_name.php";
    public final String delete_newspaper=DEFAULT_URL_PREFIX+"newspapers/delete_newspapers.php";

    // Audio Books
    public final String audio_books_create_root_category=DEFAULT_URL_PREFIX+"books/set/set_root_category.php";
    public final String audio_books_create_sub_category=DEFAULT_URL_PREFIX+"books/set/set_sub_category.php";
    public final String audio_books_create_audio_book=DEFAULT_URL_PREFIX+"books/set/set_book.php";
    public final String audio_books_create_chapter=DEFAULT_URL_PREFIX+"books/set/set_chapter.php";
    public final String audio_books_get_root_categories=DEFAULT_URL_PREFIX+"books/get/get_parent_categories.php";
    public final String audio_books_get_sub_categories=DEFAULT_URL_PREFIX+"books/get/get_sub_categories.php";
    public final String audio_books_get_all_categories=DEFAULT_URL_PREFIX+"books/get/get_categories.php";
    public final String audio_books_get_books=DEFAULT_URL_PREFIX+"books/get/get_books.php";
    public final String audio_books_books_by_category=DEFAULT_URL_PREFIX+"books/get/get_books_by_category.php";
    public final String audio_books_get_chapter=DEFAULT_URL_PREFIX+"books/get/get_chapters.php";
    public final String webView="https://admin.rcajmer.in/audio-panel/index.php";
    public final String delete_audio_book=DEFAULT_URL_PREFIX+"books/delete_book.php";
    public final String delete_chapter = DEFAULT_URL_PREFIX + "books/delete_chapter.php";
    public final String get_chapter_labels = DEFAULT_URL_PREFIX + "books/get_chapter_labels.php";
    public final String get_recently_uploaded_books = DEFAULT_URL_PREFIX + "books/get/get_recently_uploaded_books.php";

    // RC Matrimonial Meet
    public final String rc_matrimonial_meet_form=DEFAULT_URL_PREFIX+"forms/rc_matrimonial_meet_form.php";

    // Be My Writer
    public final String be_my_writer_form=DEFAULT_URL_PREFIX+"forms/be_my_writer_form.php";

    //Notification
    public final String notification_setup=DEFAULT_URL_PREFIX+"fcm/setup_notification.php";
    public final String send_notification_to_all=DEFAULT_URL_PREFIX+"fcm/send_notification_to_all_users.php";

    //send feedback
public final String sendFeedback=DEFAULT_URL_PREFIX+"books/send_feedback.php";

//library
    public final String addToLibrary=DEFAULT_URL_PREFIX+"/library/add_book_to_library.php";
    public final String fetch_library_item=DEFAULT_URL_PREFIX+"library/fetch_library_books.php";
    public final String remove_from_library=DEFAULT_URL_PREFIX+"library/remove_book_from_library.php";

    //hamrahi.com
    public final String add_profile=DEFAULT_URL_PREFIX+"rc_matrimonial/add_new_profile.php";
    public final String get_profiles=DEFAULT_URL_PREFIX+"rc_matrimonial/get_matrimonial_profiles.php";
    public final String sendRequest=DEFAULT_URL_PREFIX+"rc_matrimonial/get_request_response.php";
    public final String profileUploadRequest = DEFAULT_URL_PREFIX+"rc_matrimonial/profile_upload_request.php";
    public final String delete_profile = DEFAULT_URL_PREFIX + "rc_matrimonial/delete_profile.php";

//subscription
public final String setSubscription=DEFAULT_URL_PREFIX+"paymentGateway/setSubscription.php";
    public final String setMonthlySubscription=DEFAULT_URL_PREFIX+"paymentGateway/setMonthlySubscription.php";
    public final String getSubscription=DEFAULT_URL_PREFIX+"paymentGateway/getSubscription.php";
    public final String tmp_getSubscription=DEFAULT_URL_PREFIX+"paymentGateway/tmp_getSubscription.php";
    public final String getAmount=DEFAULT_URL_PREFIX+"paymentGateway/getSubscriptionAmount.php";
    public final String editSubscription = DEFAULT_URL_PREFIX + "paymentGateway/edit_subscription.php";
    public  final String updateSubscription=DEFAULT_URL_PREFIX+"paymentGateway/updateSubscription.php";
    public  final String updateMonthlySubscription=DEFAULT_URL_PREFIX+"paymentGateway/updateMonthlySubscription.php";



    //WhatsApp Request
    public final String whatsapp_set_request=DEFAULT_URL_PREFIX+"whatsapp/set_request.php";

}

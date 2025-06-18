package ru.crmkurgan.main.Constants;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public static String BASEURL = "https://crmkurgan.ru/smart";
    public static String CONNECTION = BASEURL +"/api.php?";

    public static String SIGNUP = CONNECTION + "signup";
    public static String SETTINGAPP = CONNECTION + "settingapp";
    public static String EDITPROFILE = CONNECTION + "editprofile";
    public static String USERDATA = CONNECTION + "userdata";
    public static String UPLOADIMAGES = CONNECTION + "uploadImages";
    public static String DELETEIMAGES = CONNECTION + "deleteImages";
    public static String FEATURED = CONNECTION + "featuredproperty";
    public static String CATEGORY = CONNECTION + "category";
    public static String CLIENT = CONNECTION + "client";
    public static String BALKON = CONNECTION + "balkon";
    public static String PLANIR = CONNECTION + "planir";
    public static String SANUZEL = CONNECTION + "sanuzel";
    public static String STEKLO = CONNECTION + "steklo";
    public static String STENA = CONNECTION + "stena";
    public static String PEREPLAN = CONNECTION + "pereplan";
    public static String POSTROY = CONNECTION + "postroy";
    public static String REMONT = CONNECTION + "remont";
    public static String WIND = CONNECTION + "wind";
    public static String RAION = CONNECTION + "raion";
        public static String TABL = CONNECTION + "tabl";
    public static String CITY = CONNECTION + "city";
    public static String POPULAR = CONNECTION + "popularproperty";
    public static String BYLATLNG = CONNECTION + "getbylatlng";
    public static String ALLPOPULAR = CONNECTION + "allpopularproperty";
    public static String ALLLATEST = CONNECTION + "alllatestproperty";
    public static String LATEST = CONNECTION + "latestproperty";
    public static String PROPPERTYDETAIL = CONNECTION + "propid=";
    public static String BYCAT = CONNECTION + "cid=";
    public static String BYTABL = CONNECTION + "tabl=";
    public static String BYTABL1 = CONNECTION + "tabl1=";
    public static String BYTABL2 = CONNECTION + "tabl2=";
    public static String BYTABLCAT = CONNECTION + "tabltocat=";
    public static String BYCITY = CONNECTION + "cityid=";
    public static String SEARCH = CONNECTION + "searchtext=";
    public static String FILTERPRICE = CONNECTION + "filterprice=";
    public static String DISTANCE = CONNECTION + "distance&user_lat=";
    public static String ALLPROPERTY = CONNECTION + "allproperty";
    public static String ALLPROPERTY1 = CONNECTION + "allproperty1";
    public static String MYPROPERTY = CONNECTION + "myproperty=";
    public static String DELETEPROPERTY = CONNECTION + "deleteproperty=";
    public static String ADDPROPERTY = CONNECTION + "addproperty";
    public static String RATING = CONNECTION + "proprating=";
    public static String SENDMESSAGE = CONNECTION + "sendmessage";

    public static String FILTERCATID="";
    public static String FILTERCITYID="";

    public static String pref_name = "pref_name";
    public static String f_name="f_name";
    public static String l_name="l_name";
    public static String uid="uid";
    public static String u_pic="u_pic";
    public static String agent="agent";
    public static String Lat="Lat";
    public static String Lon="Lon";
    public static String device_token="device_token";

    public static String versionname="1.0";

    public static int permission_camera_code=786;
    public static int permission_write_data=788;
    public static int permission_Read_data=789;
    public static int permission_Recording_audio=790;
    public static int Select_image_from_gallry_code=3;
    public static int successmsg;

    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.getDefault());

    public static String apiKey = "${MAPS_API_KEY}";
    public static RectangularBounds BOUNDS = RectangularBounds.newInstance(
            new LatLng(-33.880490, 151.184363),
            new LatLng(-33.858754, 151.229596));
}



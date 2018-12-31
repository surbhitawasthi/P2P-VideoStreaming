package Test.autoCompleteTextFeild;

import com.google.common.net.UrlEscapers;

import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExDemo {

    public static void main(String[] args) throws Exception {
//        Pattern p = Pattern.compile("[A-Za-z0-9]");
//        String s = "bA";
//
//        Matcher m = p.matcher(s);
//        System.out.println(m.find());

        String a = "Ik Kahani Song  Gajendra Verma  Vikram Singh Ft Halina K  TSeries.mp4";

       // URI uri = new URI(a);

        System.out.println(URLEncoder.encode(a,"Windows-1252"));
        String encodedString = UrlEscapers.urlFragmentEscaper().escape(a);
        System.out.println(encodedString);

    }
}

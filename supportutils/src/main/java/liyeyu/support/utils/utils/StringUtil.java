package liyeyu.support.utils.utils;

import android.text.InputFilter;
import android.text.TextUtils;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class StringUtil {

    public static boolean isBlank(String str) {
        return TextUtils.isEmpty(str);
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isPhone(String mobiles) {
        Pattern p = Pattern
                .compile("^13[0-9]{9}|14[57][0-9]{8}|17[0678][0-9]{8}|18[0-9]{9}|15[012356789][0-9]{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isIdCard(String idCard) {
        Pattern p = Pattern.compile("(^([0-9]{17})([0-9Xx]{1})$)|(^([0-9a-zA-Z]{8})\\-([0-9a-zA-Z]{1})$)");
        Matcher m = p.matcher(idCard);
        return m.matches();

    }

    public static boolean isEmail(String email) {
        Pattern pattern = Pattern.compile(
                "[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?");
        Matcher m = pattern.matcher(email);
        return m.matches();
    }

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches())
            flag = true;
        return flag;
    }

    public static boolean isEnglish(String str) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static boolean isChinese(String str) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher m = p.matcher(str);
        return m.find();
    }

    public static String getSmsNumber(String sms) {
        Pattern pattern = Pattern.compile("\\d{6}");
        Matcher matcher = pattern.matcher(sms);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }


    public static String getUrl(String value) {
        String reg = "(?=http://)(?:[^.\\s]*?\\.)+(gif)";
        Pattern p = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        boolean b = m.find();
        if (b == true) {
            return m.group(0);
        }
        return null;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String getMsToTime(String time) {
        try {
            Calendar c = Calendar.getInstance();
            c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
            return c.getTimeInMillis() + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getTimeToMs(long seconds) {
        Date d = new Date(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d).toString();
    }


    public static String getTimeToMs1(long seconds) {
        Date d = new Date(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(d).toString();
    }

    public static String getTimeToTime(long seconds) {
        Date d = new Date(seconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(d).toString();
    }

    public static String format(String format, Object value) {
        return String.format(format, value);
    }

    public static void limitEditTextNumber(EditText ed, int number){
        ed.setFilters(new InputFilter[]{new InputFilter.LengthFilter(number)});
    }

    public static String listToString(List<String> stringList, String str){
        if (stringList==null) {
            return "";
        }
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (String string : stringList) {
            if (flag) {
                result.append(str);
            }else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static String[] toStringArray(List<String> strList) {
        String[] array = new String[strList.size()];
        strList.toArray(array);
        return array;
    }

    public static String getEncryptIdCard(String id, int start, int end, char pattern) {
        String result = id;
        if (isIdCard(id) && !TextUtils.isEmpty(id)
                && end > start && id.length() > end) {
            char[] chars = result.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (i >= start) {
                    chars[i] = pattern;
                }
                if (i == end - 1) {
                    break;
                }
            }
            result = new String(chars);
        }
        return result;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }
}

package hymn.bible.view.cion.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;

public class StringUtil {

	private static final String regex = "^[_A-Za-z0-9-]+(.[_A-Za-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
	private final static String C_BASE_KEY_STRING = "SMARTMOUMOU2015004";

	/**
	 * MD5 ?ç∞?ù¥?Ñ∞ ?ïî?ò∏?ôî
	 * @param str
	 * @return
	 */
	public static String toEncMD5(String str)
	{
		String MD5 = ""; 
		try{
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(str.getBytes()); 
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			MD5 = null; 
		}
		return MD5;
	}
	
	/**
	 * AES ?ç∞?ù¥?Ñ∞ Î≥µÌò∏?ôî
	 * @param encrypted
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encrypted) 
	{
	    SecretKeySpec skeySpec = new SecretKeySpec( C_BASE_KEY_STRING.getBytes(), "AES");
		Cipher cipher;
		String decryptedString = "";
		byte[] decrypted;
		try {
			cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] decordedValue = Base64.decode(encrypted.getBytes(), Base64.NO_WRAP);
		    decrypted = cipher.doFinal(decordedValue);
		    decryptedString = new String(decrypted,"UTF-8");
		} catch (Exception e) {
			Log.d("Exception e", e.getMessage());
		}
		return decryptedString;
	}
	
	/**
     * AES Î∞©Ïãù?ùò ?ïî?ò∏?ôî
     * 
     * @param message
     * @return
     * @throws Exception
     */
    public static String encrypt(String message) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec( C_BASE_KEY_STRING.getBytes(), "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        if(message == null)
    		return null;
        
        byte[] dataStringBytes = message.getBytes("UTF-8");
        byte[] encrypted = cipher.doFinal(dataStringBytes);
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }
	
    /**
	 * ?ù¥Î©îÏùº ?ú†?ö®?Ñ± Ï≤¥ÌÅ¨
	 * @param check
	 * @return
	 */
	public static boolean isValidEmail(String check) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(check);
		return m.matches();
    }
	
	/**
	 * ?ù¥Î¶ÑÏù¥ ?ú†?ö®?ïú ?òï?ãù Ï≤¥ÌÅ¨ (?ïúÍ∏?, ?òÅÎ¨?, ?à´?ûêÎß? ?óà?ö©)
	 * @param s
	 * @return
	 */
	public static boolean isNameValid(String s)
	{
		String strKeywordU      = s.toUpperCase();
		Pattern p = Pattern.compile(".*[^Í∞?-?û£a-zA-Z0-9 ].*");
		Matcher m = p.matcher(strKeywordU);
		if(m.matches()) {
		   return false;
		} else {
		   return true;
		}
	}
	
	/**
	 * ?ïÑ?ù¥?îîÍ∞? ?ú†?ö®?ïú ?òï?ãù Ï≤¥ÌÅ¨ (?òÅÎ¨?, ?à´?ûêÎß? ?óà?ö©)
	 * @param s
	 * @return
	 */
	public static boolean isIDValid(String s)
	{
		String strKeywordU      = s.toUpperCase();
		Pattern p = Pattern.compile("^[a-zA-Z]{1}[a-zA-Z0-9_]{4,20}$");
		Matcher m = p.matcher(strKeywordU);
		if(m.matches()) {
		   return true;
		} else {
		   return false;
		}
	}
	/**
	 * ?ïÑ?ù¥?îîÍ∞? ?ú†?ö®?ïú ?òï?ãù Ï≤¥ÌÅ¨ (?òÅÎ¨?, ?à´?ûêÎß? ?óà?ö©)
	 * @param s
	 * @return
	 */
	public static boolean isPasswordValid(String s)
	{
		String strKeywordU      = s.toUpperCase();
		Pattern p = Pattern.compile("^[a-zA-Z0-9_]{6,20}$");
		Matcher m = p.matcher(strKeywordU);
		if(m.matches()) {
		   return true;
		} else {
		   return false;
		}
	}
	/**
	 * Ï£ºÏÜå?†ïÎ≥?
	 * @param addr
	 * @return
	 */
	public static String getShortAddr(String addr) 
	{
		if( addr == null)
			return "";
		
		String[] values = addr.split(" ");
		
		if( values.length == 0 ) {
			return addr;
		}
		else if( values.length == 1 ) {
			return values[0];
		}
		else if( values.length == 2 ) {
			return values[0] + " " + values[1];
		}
		else {
			return values[0] + " " + values[1] + " " + values[2];
		}
	}
	/**
	 * Î¨∏Ïûê?ó¥?ù¥ ?†ï?àò?à´?ûêÎ°úÎßå ?ù¥Î£®Ïñ¥Ï°åÎäîÏß? ÎπÑÍµê
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value) 
	{
		if( value == null || value.length()  == 0 )
			return false;
		
		for(char c : value.toCharArray() ) {
			if( c < '0' || c > '9')
				return false;
		}
		
		return true;
	}
	
	/**
	 * ???ÉÅ ObjectÍ∞? ?Ñê?ù¥Î©? Í∏∞Î≥∏ ÎπàÎ¨º?ûê?ó¥?ù¥ Î¶¨ÌÑ¥?ù¥ ?êòÎ©? ?Ñê?ù¥ ?ïÑ?ãêÍ≤ΩÏö∞ ?ûÖ?†• ?åå?ùºÎØ∏ÌÑ∞Î•? ToString() Ï≤òÎ¶¨ ?ïú?ã§.
	 * @param object : ToString ???ÉÅ Object
	 * @return parameter objecct.ToString() 
	 */
	public static String nvl(Object object) {
		return object != null ? object.toString() : "";
	}
		
	/**
	 * Î¨∏Ïûê?ó¥?ù¥ null ?ùº?ïå ?ûÑ?ùò?ùò Î¨∏Ïûê?ó¥?ùÑ Î∞òÌôò?ïú?ã§.
	 * @param  String value, String defaultValue
	 * @return String
	 * @throws
	 */
	public static String nvl(String value, String defaultValue) {
	    return (value == null || "".equals(value)) ? defaultValue : value.trim();
	}

	public static String nvl(Object o, String defaultValue) {
	    return (o == null) ? defaultValue : o.toString().trim();
	}
	
	public static String substrMax(String str, int maxlength){
		if( str == null) return "";
		return str.length() >  maxlength ? str.substring(0, maxlength) : str;
	}
	
	/**
	 * <p>
	 * ?ûÖ?†• ?åå?ùºÎØ∏ÌÑ∞?ùò Î¨∏Ïûê?ó¥?ùò Ï°¥Ïû¨?ó¨Î∂? Ï≤¥ÌÅ¨
	 * </p>
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0 || "NULL".equals(str) || "null".equals(str);
	}
	
	/**
	 * Î¨∏Ïûê?ó¥?ùÑ Replace ?ïú?ã§.
	 * @param text
	 * @param repl
	 * @param with
	 * @param max
	 * @return
	 */
	public static String replace(String text, String repl, String with, int max) {
		if (isEmpty(text) || isEmpty(repl) || with == null || max == 0) {
			return text;
		}
		int start = 0;
		int end = text.indexOf(repl, start);
		if (end == -1) {
			return text;
		}
		int replLength = repl.length();
		int increase = with.length() - replLength;
		increase = (increase < 0 ? 0 : increase);
		increase *= (max < 0 ? 16 : (max > 64 ? 64 : max));
		StringBuffer buf = new StringBuffer(text.length() + increase);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = text.indexOf(repl, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}
	
	/**
	 * Object => int ?òï Î≥??ôò
	 * @param value
	 * @return
	 */
	public static int toInt(Object value) {
		int ret = -1;
		
		if(value == null || "".equals(value)) return ret;
		
		try {
			if (value instanceof java.math.BigInteger) {
				return ((java.math.BigInteger)value).intValue();
			} else if (value instanceof String) {
				return Integer.parseInt((String)value);
			} else {
				return Integer.parseInt(value.toString());
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 * 2015-02-27T09:04:51 > 2015.02.27
	 * ?Ç†ÏßúÎßå Í∞??†∏?ò§Í∏? 
	 */
	public static String getDateStr(String date, String replace) {
		String str = date.split("T")[0];
		str = str.replace("-", replace);
		return str;
	}
	
	/**
	 * 2015-02-27T09:04:51 > 2015.02.27 09:04:51
	 * ?Ç†ÏßúÎßå Í∞??†∏?ò§Í∏? 
	 */
	public static String getDateTimeStr(String date, String replace) {
		String str = "";
		String[] dateArr = date.split("T");
		str = dateArr[0].replace("-", replace) + " " + dateArr[1];
		return str;
	}
	
	/**
	 * toKSC5061 ?óê?Ñú UTF-8Î°? parsing
	 * @param s
	 * @return
	 */
	public static String toKSC5601(String s) {
		if (s == null) {
			return null;
		}
		try {
			return new String(s.getBytes("ISO-8859-1"), "UTF-8");
		} catch (Exception e) {
			return s;
		}
	}
	
	/**
	 * ?ïô?äµ?ãúÍ∞? Î∂ÑÏúºÎ°? Í≥ÑÏÇ∞?ïòÍ∏? (212 >> 03:32)
	 */
	public static String getSecondToTimeStr(int time) {
		
		StringBuffer time_str = new StringBuffer();
		
		int minute = time / 60;
		int second = time % 60;
		
		if(minute < 10) time_str.append("0" + minute);
		else time_str.append(minute);
		
		time_str.append(":");
		
		if(second < 10) time_str.append("0" + second);
		else time_str.append(second);
		
		return time_str.toString();
	}
	
	/**
	 * ?ïô?äµ?ãúÍ∞? Î∂ÑÏúºÎ°? Í≥ÑÏÇ∞?ïòÍ∏?2 (212 >> 03Î∂? 32Ï¥?)
	 */
	public static String getSecondToTimeStr2(int second) {
		
		int hh = (second / 3600);
		int mm = (second % 3600 / 60);
		int ss = (second % 3600 % 60);
		
		StringBuffer time = new StringBuffer();
		
		if(hh > 0) time.append(String.format("%02d", hh) + "?ãúÍ∞? ");
		if(mm > 0) time.append(String.format("%02d", mm) + "Î∂? ");
		time.append(String.format("%02d", ss) + "Ï¥?");
		
		return time.toString();
	}
	
	
	/**
	* 0 ~ 9,A~FÍπåÏ? Î≤îÏúÑ ?Ç¥?óê?Ñú Random ?ïòÍ≤? ?ïÑ?ä§?Ç§Í∞íÏùÑ ?Éù?Ñ±?ïú?ã§.
	*/
	public static String getGenSeqNo(int index) throws Exception {
		int count = 0;
		String tmp;
		
		byte[] randomByte = new byte[index];
		Random rr = new Random();
		for (int i = 0; i < index; i++) 
		{
			count = 48 + rr.nextInt(index);
			
			if((57 < count) && (count < 65))
				count += 7;
			randomByte[i] = (byte)count;
		}

		tmp = new String(randomByte);
		return tmp;
	}
	
	private static char[] _randomKeyLChar;
	
	/**
	 * ?ûÑ?ùò?Ç§ ?Éù?Ñ±
	 */
	public static String getRandomKey(int len) throws Exception {
		
		StringBuffer buff = new StringBuffer();
		
		Random random = new Random();
		
		if( _randomKeyLChar == null ) {
			_randomKeyLChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		}
		
		for ( int ii = 0; ii < len; ii++) {
			buff.append(_randomKeyLChar[random.nextInt(_randomKeyLChar.length)]);
		}
		
		return buff.toString();
	}
	
	/**
	 * Î¨∏Ïûê?ó¥ Ï§ëÎ≥µ?êúÍ∞? ?†úÍ±?
	 * @param str
	 * @return
	 */
	public static String getRepeatRemove(String str) 
	{
		StringBuilder sb = new StringBuilder();
		String[] tokens = str.split(",");
		
		ArrayList<String> list = new ArrayList<String>();
		
		for( String msg : tokens)
		{
			list.add(msg);
		}

		ArrayList<String> nonDupList = new ArrayList<String>();
		
		Iterator<String> dupIter = list.iterator();
		
		while(dupIter.hasNext())
		{
			String msg = dupIter.next();
			
			if(nonDupList.contains(msg))
			{
				dupIter.remove();
			}else
			{
				nonDupList.add(msg);
			}
		}
		
		for(int i = 0; i < nonDupList.size(); i++)
		{
			String s = nonDupList.get(i);
			if( i == nonDupList.size() - 1)
			{
				sb.append(s);
			}else{
				sb.append(s + ",");
			}
		}
		return sb.toString();		
	}
	
	public static InputFilter filterAlphaNum = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			} 
			return null;
		} 
	};
	
	public static InputFilter filterPassWord = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			Pattern ps = Pattern.compile("^[a-zA-Z0-9@!#*[$]]+$");			
			if (!ps.matcher(source).matches()) {
				return "";
			} 
			return null;
		} 
	};
	
	public static InputFilter filterEmail = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {

			Pattern ps = Pattern.compile("^[@_a-zA-Z0-9-\\.]+$");			
			if (!ps.matcher(source).matches()) {
				return "";
			} 
			return null;
		} 
	};
	
	public static String setDate(String paramString){
		String str1 = paramString;
		try{
	      paramString = paramString.replace("  ", " ");
	      String str2 = new SimpleDateFormat("yy.MM.dd.HH.mm.ss").format(new Date(Date.parse(paramString)));
	      str1 = str2;
	      return str1;
	    }catch (NullPointerException localNullPointerException){
	    	while (true)
	        str1 = "00.00.00 00.00.00";
	    }catch (IllegalArgumentException localIllegalArgumentException){
	    	while (true)
	        str1 = setDate2(paramString);
	    }
	}
	
	public static String setDate2(String paramString){
		String str1 = paramString;
	    try{
	    	String str2 = paramString.replace("KST", "+0900");
	    	String str3 = new SimpleDateFormat("yy.MM.dd.HH.mm.ss").format(new Date(Date.parse(str2)));
	    	str1 = str3;
	    	return str1;
	    }catch (NullPointerException localNullPointerException){
	    	while (true)
	        str1 = "00.00.00 00.00.00";
	    }catch (IllegalArgumentException localIllegalArgumentException){
	    	while (true)
	        str1 = "00.00.00 00.00.00";
	    }
	}
	
	public static String setDateTrim(String paramString){
		return paramString.substring(0, 8);
	}
	
	public static String getExtension(String fileStr) {
		return fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
	}
	
	public static int getRandomNumber(){
		return 1000 + (int)(9000.0D * Math.random());
	}
	
	public static String getUrlType(String s, String s1){
    	String as[] = s.split("url=");
    	String s2 = null;
    	int i = 0;
        do{
            if(i >= as.length)
                return s2;
            if(as[i].contains("medium") && as[i].contains(s1))
                s2 = removeItag2(removeComma(removeItag(removeCodecs(as[i]))));
            i++;
        } while(true);
    }
	
	public static String removeCodecs(String s){
        if(s.indexOf("codecs") > -1)
        {
            int i = s.indexOf(";");
            int j = s.indexOf("&", i);
            if(j == -1)
                j = -1 + s.length();
            String s1 = s.substring(0, i);
            String s2 = s.substring(j);
            if(s2.length() == 1)
                s = s1;
            else
                s = (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        }
        return s;
    }
	
	public static String removeItag(String s)
    {
        if(getStringPatternCount(s, "&itag=") > 1)
        {
            int i = s.indexOf("&itag=");
            int j = s.indexOf("&", i + 1);
            String s1 = s.substring(0, i);
            String s2 = s.substring(j);
            s = (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        }
        return s;
    }
	
	public static int getStringPatternCount(String s, String s1)
    {
        int i = 0;
        Matcher matcher = Pattern.compile(s1).matcher(s);
        int j = 0;
        do
        {
            if(!matcher.find(i))
                return j;
            j++;
            i = matcher.end();
        } while(true);
    }
	
	public static String removeComma(String s)
    {
        if(s != null && s.endsWith(","))
            s = s.substring(0, -1 + s.length());
        return s;
    }
	
	public static String removeItag2(String s)
    {
        if(getStringPatternCount(s, "itag=") > 1)
        {
            int i = s.indexOf("itag=");
            int j = s.indexOf("&", i + 1);
            String s1 = s.substring(0, i);
            String s2 = s.substring(j);
            s = (new StringBuilder(String.valueOf(s1))).append(s2).toString();
        }
        return s;
    }
}
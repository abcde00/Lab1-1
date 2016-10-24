package lab1;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZouKaifa on 2016/9/5.
 */
public class Main {
    
    /**.
     * 澶氶」寮忕被锛屾湁鎻忚堪澶氶」寮忕殑鍚勭灞炴��
     */
    private class Expression {
        
        private String[] vars;  //鍙橀噺鏁扮粍
        
        private int length;  //椤规暟.
        
        private double[][] values;  //姣忎竴椤逛腑甯告暟鍙婂悇涓
        //彉閲忕殑娆℃暟锛堝垪涓嬫爣浠ｈ〃椤癸紝绗竴琛屼负甯告暟锛岀浜岃寮�濮嬩负鍙橀噺锛�.
        
        private String exp;  //鏍囧噯鐨勬牸寮忓寲瀛楃涓插舰寮�.
    }
    
    private Expression exp;  //褰撳墠澶勭悊鐨勮〃杈惧紡
    
    /**.
     * 鑾峰緱褰撳墠鐨勮〃杈惧紡
     *
     * @return 褰撳墠琛ㄨ揪寮�
     */
    public final Expression getExp() {
        return exp;
    }
    
    /**.
     * 璁剧疆姝ｅ湪澶勭悊鐨勮〃杈惧紡
     *
     * @param exp 琛ㄨ揪寮�
     */
    private void setExp(Expression  exp) {
        this.exp = exp;
    }
    
    /**.
     * 琛ㄨ揪寮忓垽鏂�佸鐞�
     *
     * @param expStr 琛ㄨ揪寮忓瓧绗︿覆
     * @return 鐢熸垚鐨勮〃杈惧紡瀵硅薄
     */
    private Expression expression(String expStr) {
        Pattern single = Pattern.compile("((\\d+)|([a-zA-Z]+(\\s*\\^\\s*[1-9][0-9]*)?))");
        //瀛楁瘝/鏁板瓧
        Pattern nape = Pattern.compile(single + "((\\s*\\*\\s*)?" + single + ")*");  //涓�椤�
        Pattern pattern = Pattern.compile("\\s*-?" + nape + "(\\s*[+-]\\s*" + nape + ")*");  //鏁翠釜琛ㄨ揪寮�
        Matcher matcher = pattern.matcher(expStr);
        if (matcher.find() && matcher.group().equals(expStr)) {  //鍖归厤鎴愬姛
            return deal(expStr);
        }
        return null;
    }
    
    /**.
     * 瀵规纭殑瀛楃涓茶繘琛屽鐞嗭紝鍖呮嫭鍚湁灏忔暟銆佽礋鏁扮殑瀛楃涓�
     *
     * @param expStr
     * @return Expression
     */
    private Expression deal(String expStr) {
        Expression expr = new Expression();
        Pattern tSingle = Pattern.compile("((\\d+(\\.\\d+)?)|([a-zA-Z]+(\\s*\\^\\s*[1-9][0-9]*)?))");
        //鍙娴嬪皬鏁�
        
        //鍚皬鏁扮殑椤�
        expr.length = expStr.split("[+-]").length;  //椤规暟
        if (expStr.startsWith("-")) {  //涓嶇畻绗竴椤圭殑璐熷彿
            expr.length -= 1;
        }
        
        TreeSet<String> varSet = new TreeSet<>();  //浣跨敤TreeSet鏀堕泦鍙橀噺鏁�
        Matcher varMatch = Pattern.compile("[a-zA-Z]+").matcher(expStr);
        while (varMatch.find()) {
            varSet.add(varMatch.group());
        }
        expr.vars = new String[varSet.size()];
        expr.vars = varSet.toArray(expr.vars);  //灏唖et杞负鏁扮粍
        expr.values = new double[expr.vars.length + 1][expr.length];
        for (int i = 0; i < expr.values.length; i++) {  //鍏ㄨ祴0渚夸簬鍖栫畝锛屽父鏁拌祴1
            for (int j = 0; j < expr.values[0].length; j++) {
                expr.values[i][j] = i == 0 ? 1 : 0;
            }
        }
        expStr = expStr.replaceAll("\\s", "");  //鍘荤┖鏍�
        Pattern tNape = Pattern.compile(tSingle + "((\\s*\\*\\s*)?" + tSingle + ")*");
        Matcher valMatch = tNape.matcher(expStr);
        int j = 0;  //鍒椾笅鏍�
        while (valMatch.find()) {
            String val = valMatch.group();
            Matcher sin = tSingle.matcher(val);
            while (sin.find()) {  //鍒嗘瀽鍗曢」鐨勬垚鍒�
                String re = sin.group();
                if (re.matches("\\d+(\\.\\d+)?")) {  //甯告暟鍒欑洿鎺ョ浉涔�
                    expr.values[0][j] *= Double.parseDouble(re);
                } else {  //鍙橀噺鍒欐鏁扮浉鍔�
                    String str = re.contains("^") ? re.split("\\s*\\^\\s*")[0] : re;
                    for (int i = 0; i < expr.vars.length; i++) {  //鎵惧琛屼笅鏍�
                        if (expr.vars[i].equals(str)) {
                            expr.values[i + 1][j] += re.contains("^") ? Double.parseDouble(re.split("\\s*\\^\\s*")[1]) : 1;
                            break;
                        }
                    }
                }
            }
            int index = valMatch.start();
            if (j > 0 || expStr.startsWith("-")) {
                if (expStr.charAt(index - 1) == '-') {  //璇ラ」鍓嶉潰鏄惁瀛樺湪璐熷彿
                    expr.values[0][j] *= -1;
                }
            }
            j++;
        }
        generate(expr);
        return expr;
    }
    
    
    /**.
     * 琛ㄨ揪寮忕畝鍖栨眰鍊�
     *
     * @param expStr 鍙橀噺璧嬪�煎紡
     * @return 绠�鍖栧悗鐨勮〃杈惧紡瀵硅薄
     */
    private Expression simplify(String expStr) {
        if ((Pattern.compile(
                             "!\\s*simplify(\\s*[a-zA-z]+\\s*=\\s*([+-]?(\\d+(\\.\\d+)?))\\s*)*")
             ).matcher(expStr).find()) {  //绗﹀悎璇硶
            Matcher assign = Pattern.compile(
                                             "[a-z]+\\s*=\\s*([+-]?(\\d+(\\.\\d+)?))").matcher(expStr);  //鍖归厤璧嬪�艰鍙�
            String str = exp.exp;  //鏍煎紡鍖栧瓧绗︿覆
            /*
             浣跨敤鏍煎紡鍖栧悗鐨勫瓧绗︿覆锛屽厛灏嗗箓鏇挎崲涓烘暟瀛楋紝鍐嶅皢1娆″箓鏇挎崲锛屽啀浣跨敤expression鏂规硶灏嗘浛鎹�
             鍚庣殑瀛楃涓茶浆涓篍xpression瀵硅薄
             */
            while (assign.find()) {
                String ass = assign.group().replaceAll("\\s", "");
                String var = ass.split("=")[0];  //鍙橀噺鍙婂叾鍊�
                double value = Double.parseDouble(ass.split("=")[1]);
                Matcher varMatch = Pattern.compile("\\b" + var + "\\b\\^\\d+").matcher(str);  //鍖归厤骞傞」
                while (varMatch.find()) {
                    String t = varMatch.group();
                    double newValue = Math.pow(value, Integer.parseInt(t.split("\\^")[1]));
                    //鑻ュ�间负璐熸暟锛屽垯浠呮敼鍙樿繖涓�椤圭殑绗﹀彿
                    if (newValue < 0) {
                        str = changeSymbol(str, t);
                    }
                    varMatch = Pattern.compile("\\b" + var + "\\b\\^\\d+").matcher(str);
                    varMatch.find();
                    str = varMatch.replaceAll(String.valueOf(Math.abs(newValue)));  //鏇挎崲
                    varMatch = Pattern.compile("\\b" + var + "\\b\\^\\d+").matcher(str);  //閲嶆柊鍖归厤
                }
                //鍐嶅崟鐙浛鎹�1娆″彉閲�
                if (value < 0) {
                    str = changeSymbol(str, var);
                }
                Matcher letterMatch = Pattern.compile("\\b" + var + "\\b").matcher(str);
                if (letterMatch.find()) {
                    str = letterMatch.replaceAll(String.valueOf(Math.abs(value)));
                }
            }
            return deal(str);
        }
        return null;
    }
    
    
    /**.
     * 骞傛垨鍙橀噺鏇挎崲缁撴灉鑻ヤ负璐熸暟锛屽垯涓嶄娇鐢ㄨ礋鏁版浛鎹紝鑰屾敼鍙樺叾鎵�鍦ㄩ」鍓嶉潰鐨勭鍙�
     *
     * @param originStr 鍘熷椤瑰紡瀛椾覆
     * @param var       浼氳鏇挎崲涓鸿礋鏁扮殑骞傛垨鍙橀噺
     * @return 鏀瑰彉绗﹀彿鍚庣殑瀛楃涓诧紙鏈繘琛屽彉閲忔浛鎹紝浠呮敼鍙橀」鐨勭鍙凤級
     */
    private String changeSymbol(String originStr, String var) {
        Matcher matcher;
        if (var.contains("^")) {
            matcher=Pattern.compile("\\b"+var.split("\\^")[0]+"\\b\\^"+var.split("\\^")[1]).matcher(originStr);
        } else {
            matcher = Pattern.compile("\\b" + var + "\\b").matcher(originStr);
        }
        matcher.reset();
        if (matcher.find()) {
            Matcher originMatch = matcher.pattern().matcher(originStr);
            originMatch.reset();
            int add = 0;  //鏄惁鍦ㄥ墠闈㈠姞浜嗚礋鍙锋垨鍘绘帀
            while (originMatch.find()) {
                int index = originMatch.start() + add;  //鏇挎崲浣嶇疆鐨勭储寮�
                StringBuffer temp = new StringBuffer(originStr);
                while (index >= 0) {  //鍚戝墠瀵绘壘鏈�杩戠殑鍔犲噺鍙�
                    if (temp.charAt(index) == '+') {
                        temp.setCharAt(index, '-');
                        originStr = temp.toString();
                        break;
                    } else if (temp.charAt(index) == '-') {
                        if (index == 0) {  //鑻ヨ礋鍙峰凡澶勪簬鏈�寮�濮嬶紝鍒欏皢璐熷彿鍘绘帀
                            originStr = originStr.substring(1);
                            add--;
                        } else {
                            temp.setCharAt(index, '+');
                            originStr = temp.toString();
                        }
                        break;
                    }
                    index--;
                }
                if (index == -1 && !originStr.startsWith("-")) {  //鏈壘鍒扮鍙凤紝鍗宠椤逛负棣栭」涓旀
                    originStr = "-" + originStr;
                    add++;
                }
            }
            return originStr;
        }
        return originStr;
    }
    
    /**.
     * 琛ㄨ揪寮忔眰瀵�
     *
     * @param expStr 姹傚鍙橀噺瀛楃涓�
     * @return 姹傚鍚庣殑琛ㄨ揪寮忓璞�
     */
    private Expression derivative(String expStr) {
        Expression newExp = new Expression();
        //澶嶅埗鍘焑xp鐨勬暟鎹�
        newExp.values = new double[exp.values.length][exp.length];
        for (int i = 0; i < exp.values.length; i++) {
            System.arraycopy(exp.values[i], 0, newExp.values[i], 0, exp.length);
        }
        newExp.vars = exp.vars.clone();
        newExp.length = exp.length;
        newExp.exp = exp.exp;
        Pattern pat = Pattern.compile("!\\s*d/d\\s*[a-zA-z]+");
        Matcher match = pat.matcher(expStr);  //璇硶妫�娴�
        if (match.find()) {
            expStr = expStr.replace("d/d", "");
            Matcher varMatch = Pattern.compile("[a-zA-z]+").matcher(expStr);  //瀵绘壘姹傚鐨勫彉閲�
            int time = 0;
            String va = "";
            while (varMatch.find()) {  //鍒ゆ柇姹傚鐨勫彉閲忎釜鏁版槸鍚︿负1
                va = varMatch.group();
                time++;
            }
            boolean pass = false;
            int index = 0;
            for (int i = 0; i < newExp.vars.length; i++) {
                if (newExp.vars[i].equals(va)) {  //鏄惁瀛樺湪璇ュ彉閲�
                    pass = true;
                    index = i;
                    break;
                }
            }
            index++;
            if (pass && time == 1) {  //姹傚锛屽綋涓斾粎褰撳瓨鍦ㄥ彉閲忎笖鍙湁涓�涓�
                for (int i = 0; i < newExp.length; i++) {  //姣忎釜椤规尐涓眰瀵�
                    if (newExp.values[index][i] >= 1) {
                        newExp.values[0][i] *= newExp.values[index][i];
                        newExp.values[index][i] -= 1;
                    } else if (newExp.values[index][i] == 0) {  //鑻ユ鏁颁负0锛屽垯璇ラ」绯绘暟鍙�0
                        newExp.values[0][i] = 0;
                    }
                }
                generate(newExp);
                return newExp;
            }
            return null;
        }
        return null;
    }
    
    /**.
     * 鏍规嵁vars鍙妚alues鏁扮粍锛岃繘琛屽椤瑰紡鐨勫悓绫婚」鍚堝苟锛屼互鍙婂瓧绗︿覆褰㈠紡鍐嶇敓鎴�(鐩存帴瀵瑰師澶氶」寮忎慨鏀�)
     */
    private void generate(Expression ex) {
        TreeMap<String, Double> napeMap = new TreeMap<>();  //鐢ㄤ簬鍚堝苟鍚岀被椤�
        
        /*
         * 鍏堢敓鎴愭瘡涓�椤圭殑娆℃暟瀛楃涓诧紙濡倄^2*y^3*z锛屽垯瀛楃涓蹭负"2 3 0"锛�
         * 锛屽湪TreeMap閲屼互瀛楃涓蹭负key灏嗘鏁扮浉鍔犱粠鑰屽畬鎴愬悎骞讹紝鍐嶈繕鍘熶负鏁扮粍褰㈠紡
         */
        for (int i = 0; i < ex.length; i++) {
            String key = "";
            for (int j = 1; j < ex.values.length - 1; j++) {  //涓嶉渶缁熻甯告暟鐨勬鏁�
                key += ex.values[j][i] + " ";
            }
            if (ex.values.length > 1) {  //鏈�鍚庝竴涓彉閲忓崟鐙姞鍏ュ瓧绗︿覆锛岄伩鍏嶇┖鏍煎姞鍏�
                key += ex.values[ex.values.length - 1][i];
            }
            if (napeMap.containsKey(key)) {  //瀛椾覆瀛樺湪鍒欑浉鍔狅紙鍗冲悎骞讹級
                napeMap.put(key, napeMap.get(key) + ex.values[0][i]);
            } else {
                napeMap.put(key, ex.values[0][i]);
            }
            if (napeMap.get(key) == 0) {  //鑻ヨ椤圭郴鏁颁负0锛屽垹鎺�
                napeMap.remove(key);
            }
        }
        
        ex.length = napeMap.size();
        ex.values = new double[ex.vars.length + 1][ex.length];  //閲嶆柊杩樺師涓烘暟缁�
        int i = 0;
        for (Map.Entry<String, Double> pair : napeMap.entrySet()
             ) {  //閬嶅巻TreeMap锛屼负鏁扮粍璧嬪��
            String[] p = pair.getKey().split(" ");
            ex.values[0][i] = pair.getValue();  //甯告暟
            if (!pair.getKey().equals("")) {  //绌哄瓧涓茶鏄庢棤鍙橀噺
                for (int j = 0; j < p.length; j++) {
                    ex.values[j + 1][i] = Double.parseDouble(p[j]);
                }
            }
            ++i;
        }
        //鍐嶇敓鎴愭牸寮忓寲鐨勫瓧绗︿覆
        ex.exp = "";
        for (int j = 0; j < ex.length; j++) {
            if (ex.values[0][j] < 0) {  //姝ｈ礋鍙�
                ex.exp += "-";
            } else if (j > 0) {
                ex.exp += "+";
            }
            if (ex.values[0][j] == (int) (ex.values[0][j])) {  //鑻ヤ负鏁存暟鍒欏己杞负int锛岄伩鍏嶅嚭鐜皒.0
                ex.exp += Math.abs((int) (ex.values[0][j]));
            } else {
                DecimalFormat df = new DecimalFormat("#.######");
                String t = df.format(Math.abs(ex.values[0][j]));
                ex.exp += t;  //娴偣鏁�
            }
            for (int k = 0; k < ex.vars.length; k++) {  //鍙橀噺澶勭悊
                if (ex.values[k + 1][j] > 0) {
                    ex.exp += "*" + ex.vars[k];
                    if (ex.values[k + 1][j] > 1) { //鍚湁骞�
                        ex.exp += "^" + (int) ex.values[k + 1][j];  //娆℃暟涓烘暣鏁帮紝鐩存帴鍙栨暣
                    }
                }
            }
        }
        //鑻ユ槸涔�1鍒欏幓鎺�
        ex.exp = ex.exp.replaceAll("^1\\*", "");  //鏈�寮�濮嬬殑1
        ex.exp = ex.exp.replaceAll("\\+1\\*", "+");
        ex.exp = ex.exp.replaceAll("-1\\*", "-");
        if (ex.exp.equals("")) {
            ex.exp = "0";
        }
    }
    
    /**.
     * 涓庣敤鎴疯繘琛屼氦浜掞紝璋冪敤鍏跺畠鍑芥暟杩涜澶勭悊
     */
    public void answer() {
        Scanner scan = new Scanner(System.in);
        DecimalFormat df = new DecimalFormat("#.###");
        while (true) {
            String line = scan.nextLine();  //璇诲彇杈撳叆
            if (getExp() != null && line.matches("^!\\s*simplify.*")) {  //绠�鍖�
                long time = System.nanoTime();
                Expression sim = simplify(line);
                long useTime = System.nanoTime() - time;
                System.out.println("Used time: " + df.format(useTime / 1000000.0) + "ms, Result:");
                if (sim == null) {
                    System.out.println("Wrong Assignment!");
                } else {
                    System.out.println(sim.exp);
                }
            } else if (getExp() != null && line.matches("^!\\s*d/d.*")) {  //姹傚
                long time = System.nanoTime();
                Expression der = derivative(line);
                long useTime = System.nanoTime() - time;
                System.out.println("Used time: " + df.format(useTime / 1000000.0) + "ms, Result:");
                if (der == null) {
                    System.out.println("Error, no variable!");
                } else {
                    System.out.println(der.exp);
                }
            } else {  //鍏跺畠杈撳叆
                long time = System.nanoTime();
                Expression newExp = expression(line);
                long useTime = System.nanoTime() - time;
                System.out.println("Used time: " + df.format(useTime / 1000000.0) + "ms, Result:");
                if (newExp != null) {  //姝ｇ‘
                    setExp(newExp);
                    System.out.println(getExp().exp);
                } else {  //鍑洪敊
                    System.out.println("Wrong polynomial!");
                }
            }
        }
    }
    
    public static void main(String[] args) {new Main().answer();
    }
}

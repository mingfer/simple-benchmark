package cn.mingfer.benchmark;


import java.util.Collection;

/**
 * 用作参数断言的静态接口
 */
public class Args {

    /**
     * 断言表达式，当表达式不通过时抛出 {@link IllegalArgumentException}
     *
     * @param expression 表达式
     * @param message    当表达式为 false 时候的错误信息
     */
    public static void check(final boolean expression, final String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 断言表达式，当表达式不通过时抛出 {@link IllegalArgumentException}
     *
     * @param expression 表达式
     * @param message    当表达式为 false 时候的错误信息
     * @param args       错误信息的参数列表
     */
    public static void check(final boolean expression, final String message, final Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    /**
     * 断言表达式，当表达式不通过时抛出 {@link IllegalArgumentException}
     *
     * @param expression 表达式
     * @param message    当表达式为 false 时候的错误信息
     * @param arg        错误信息的参数
     */
    public static void check(final boolean expression, final String message, final Object arg) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, arg));
        }
    }

    /**
     * 断言类型是否为 null，否则抛出 {@link IllegalArgumentException}
     *
     * @param argument 断言值
     * @param name     断言值的字段名称，当异常时抛出 {@code name + " may not be null"}
     * @param <T>      断言值类型
     * @return 断言值
     */
    public static <T> T notNull(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        return argument;
    }

    /**
     * Returns true if the parameter is null or of zero length
     * @param s 字符串
     * @return true 字符串为 null 或空值
     */
    public static boolean isEmpty(final CharSequence s) {
        if (s == null) {
            return true;
        }
        return s.length() == 0;
    }

    /**
     * Returns true if the parameter is null or contains only whitespace
     * @param s 字符串
     * @return true 字符串为 null，空值或者全空格
     */
    public static boolean isBlank(final CharSequence s) {
        if (s == null) {
            return true;
        }
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 断言参数不为空，否则抛出 {@link IllegalArgumentException}
     *
     * @param argument 可以进行空值检查的参数
     * @param name     参数名称
     * @param <T>      参数类型，必须是 {@link CharSequence} 的子类
     * @return 参数值
     */
    public static <T extends CharSequence> T notEmpty(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (isEmpty(argument)) {
            throw new IllegalArgumentException(name + " may not be empty");
        }
        return argument;
    }

    /**
     * 断言参数是否仅为空格，仅为空格抛出 {@link IllegalArgumentException}
     *
     * @param argument 进行检查的参数
     * @param name     参数名称
     * @param <T>      参数类型，必须是 {@link CharSequence} 的子类
     * @return 参数值
     */
    public static <T extends CharSequence> T notBlank(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (isBlank(argument)) {
            throw new IllegalArgumentException(name + " may not be blank");
        }
        return argument;
    }

    /**
     * 断言参数没有包含空格，包含空格抛出 {@link IllegalArgumentException}
     *
     * @param argument 进行检查的参数
     * @param name     参数名称
     * @param <T>      参数类型，必须是 {@link CharSequence} 的子类
     * @return 参数值
     */
    public static <T extends CharSequence> T containsNoBlanks(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (argument.length() == 0) {
            throw new IllegalArgumentException(name + " may not be empty");
        }
        if (containsBlanks(argument)) {
            throw new IllegalArgumentException(name + " may not contain blanks");
        }
        return argument;
    }

    /**
     * 判断参数是否包含空格
     *
     * @param s 参数值
     * @return true 包含空格
     */
    public static boolean containsBlanks(final CharSequence s) {
        if (s == null) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (Character.isWhitespace(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 断言集合参数不为空，否则抛出 {@link IllegalArgumentException}
     *
     * @param argument 参数值
     * @param name     参数名称
     * @param <E>      集合中的对象类型
     * @param <T>      集合类型
     * @return 传入进行断言的参数
     */
    public static <E, T extends Collection<E>> T notEmpty(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (argument.isEmpty()) {
            throw new IllegalArgumentException(name + " may not be empty");
        }
        return argument;
    }

    /**
     * 断言传入的整数是一个正整数，否则抛出 {@link IllegalArgumentException}
     *
     * @param n    断言的整数
     * @param name 参数值名称
     * @return 传入断言的整数
     */
    public static int positive(final int n, final String name) {
        if (n <= 0) {
            throw new IllegalArgumentException(name + " may not be negative or zero");
        }
        return n;
    }

    /**
     * 断言传入的整数是一个正整数，否则抛出 {@link IllegalArgumentException}
     *
     * @param n    断言的整数
     * @param name 参数值名称
     * @return 传入断言的整数
     */
    public static long positive(final long n, final String name) {
        if (n <= 0) {
            throw new IllegalArgumentException(name + " may not be negative or zero");
        }
        return n;
    }

    /**
     * 断言传入的整数是一个非负数，否则抛出 {@link IllegalArgumentException}
     *
     * @param n    断言的整数
     * @param name 参数值名称
     * @return 传入断言的整数
     */
    public static int notNegative(final int n, final String name) {
        if (n < 0) {
            throw new IllegalArgumentException(name + " may not be negative");
        }
        return n;
    }

    /**
     * 断言传入的整数是一个非负数，否则抛出 {@link IllegalArgumentException}
     *
     * @param n    断言的整数
     * @param name 参数值名称
     * @return 传入断言的整数
     */
    public static long notNegative(final long n, final String name) {
        if (n < 0) {
            throw new IllegalArgumentException(name + " may not be negative");
        }
        return n;
    }

}

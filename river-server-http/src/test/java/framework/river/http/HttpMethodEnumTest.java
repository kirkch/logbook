package framework.river.http;

import org.junit.Test;

import java.util.Arrays;

/**
 *
 */
public class HttpMethodEnumTest {

    @Test
    public void f() {
        System.out.println("HttpMethodEnum.GET.ordinal() = " + HttpMethodEnum.GET.ordinal());
        System.out.println("HttpMethodEnum.POST.ordinal() = " + HttpMethodEnum.POST.ordinal());

        HttpMethodEnum e = HttpMethodEnum.POST;
        switch (e) {
            case GET:
                System.out.println("GET");
                break;
            case POST:
                System.out.println("POST");
                break;
        }

        System.out.println("values = " + Arrays.asList(HttpMethodEnum.values()));
    }

}

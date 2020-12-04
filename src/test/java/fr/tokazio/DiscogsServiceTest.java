package fr.tokazio;

import org.junit.jupiter.api.Test;

public class DiscogsServiceTest {

    @Test
    public void ip() {
        String url = DiscogsService.getCallbackUrl();
        System.out.println(url);
    }
}

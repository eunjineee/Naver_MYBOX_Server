package back.server.mybox.jwt;

public interface JwtProperties {
    String SECRET = "eunjins-mybox";
//    int AccessToken_TIME =  1000*60*10; //(1/1000초) //10분
    int AccessToken_TIME = 1000 * 60 * 60 * 24 * 7 ;//1 week
    int RefreshToken_TIME = 1000 * 60 * 60 * 24 * 7 ;//1 week
    String HEADER_STRING = "accessToken";
}

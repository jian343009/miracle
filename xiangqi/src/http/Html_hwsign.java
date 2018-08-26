package http;

import org.jboss.logging.Logger;

import main.ServerTimer;
import dao.Dao;

public class Html_hwsign extends Html{
	private static Logger log = Logger.getLogger(Html_hwsign.class);

	private String PrivateKey =	"MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEArrpw3FjSbMXPYUf2zBBUMF+/ZzLhM7oZN0X/IMpNiyCbwM7mr3AVz9dDdh23a964I5o8odNZEpR1YHkvIEY6OQIDAQABAkBtiWoJaRF5qUSJyvCYG0B8FvpJ+uadY/Q853+6kbitF5nNCeKxQ9mGSHMP6oTa9tQEHA3U9JcZg1zvN2ZRvh8RAiEA29VGXZ78k+nFJVH5GVD+V7stEoXncel8Ydick4C3Yf0CIQDLeXu9F5BIjdvf91UYmM6AAXC11SW4TYlD/EeFo7R/7QIhALq2X2h+84nxwIddI1RUTWJYUQTthFFk/UbhwsMpTRhFAiEArO1YPoKSOzdlENlRVxA3IA8ZTVATOKmc6Uy1NFzHAJkCIE42/82O7ctKZAUQjRcReGPCqE16cZHFZhsL+XT38hEt";
	                           //MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEArrpw3FjSbMXPYUf2zBBUMF+/ZzLhM7oZN0X/IMpNiyCbwM7mr3AVz9dDdh23a964I5o8odNZEpR1YHkvIEY6OQIDAQABAkBtiWoJaRF5qUSJyvCYG0B8FvpJ+uadY/Q853+6kbitF5nNCeKxQ9mGSHMP6oTa9tQEHA3U9JcZg1zvN2ZRvh8RAiEA29VGXZ78k+nFJVH5GVD+V7stEoXncel8Ydick4C3Yf0CIQDLeXu9F5BIjdvf91UYmM6AAXC11SW4TYlD/EeFo7R/7QIhALq2X2h+84nxwIddI1RUTWJYUQTthFFk/UbhwsMpTRhFAiEArO1YPoKSOzdlENlRVxA3IA8ZTVATOKmc6Uy1NFzHAJkCIE42/82O7ctKZAUQjRcReGPCqE16cZHFZhsL+XT38hEt
	public String getHtml(String content)
	{
		String html = RSA.sign(content, PrivateKey);
		log.info(html);
		return html;
	}
}

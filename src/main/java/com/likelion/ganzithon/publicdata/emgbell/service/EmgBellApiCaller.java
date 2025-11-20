package com.likelion.ganzithon.publicdata.emgbell.service;

import com.likelion.ganzithon.publicdata.emgbell.dto.EmgBellData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmgBellApiCaller {

    private final RestTemplate restTemplate;

    @Value("${public.api.emgbell-url}")
    private String apiUrl;

    @Value("${public.api.emgbell-key}")
    private String apiKey;

    public List<EmgBellData> fetchEmgBells(int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("type", "xml")
                .build(true)
                .toUri();

        try {
            String xml = restTemplate.getForObject(uri, String.class);
            if (xml == null || xml.isBlank()) {
                return List.of();
            }
            return parseXml(xml);
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<EmgBellData> parseXml(String xml) throws Exception {
        List<EmgBellData> result = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        NodeList items = doc.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            Element el = (Element) items.item(i);

            String objtId = getTagValue(el, "OBJT_ID");
            String fcltyTy = getTagValue(el, "FCLTY_TY");
            String mngInst = getTagValue(el, "MNG_INST");
            String insPurpos = getTagValue(el, "INS_PURPOS");
            String insType = getTagValue(el, "INS_TYPE");
            String insDetail = getTagValue(el, "INS_DETAIL");
            String rnAdres = getTagValue(el, "RN_ADRES");
            String adres = getTagValue(el, "ADRES");
            String latStr = getTagValue(el, "LAT");
            String lonStr = getTagValue(el, "LON");

            double lat = parseDoubleSafe(latStr);
            double lon = parseDoubleSafe(lonStr);

            if (Double.isNaN(lat) || Double.isNaN(lon)) {
                continue;
            }

            result.add(new EmgBellData(
                    objtId,
                    fcltyTy,
                    mngInst,
                    insPurpos,
                    insType,
                    insDetail,
                    rnAdres,
                    adres,
                    lat,
                    lon
            ));
        }

        return result;
    }

    private String getTagValue(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0) return null;
        String text = list.item(0).getTextContent();
        return text != null ? text.trim() : null;
    }

    private double parseDoubleSafe(String value) {
        try {
            return value == null || value.isBlank() ? Double.NaN : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}

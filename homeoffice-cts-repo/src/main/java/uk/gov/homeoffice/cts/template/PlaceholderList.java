package uk.gov.homeoffice.cts.template;

import org.alfresco.service.namespace.QName;
import uk.gov.homeoffice.cts.model.CtsModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by davidt on 10/11/2014.
 */
public class PlaceholderList {

    static final Placeholder CORRESPONDENT_NAME = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PROP_CORRESPONDENT_TITLE,
                    CtsModel.PROP_CORRESPONDENT_FORENAME,
                    CtsModel.PROP_CORRESPONDENT_SURNAME
            },
            " "
    );

    static final Placeholder CORRESPONDENT_ADDRESS = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE1,
                    CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE2,
                    CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE3,
                    CtsModel.PROP_CORRESPONDENT_COUNTRY,
                    CtsModel.PROP_CORRESPONDENT_POSTCODE
            },
            "\n"
    );

    static final Placeholder CORRESPONDENT_ADDRESS_COMMA = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE1,
                    CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE2,
                    CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE3,
                    CtsModel.PROP_CORRESPONDENT_COUNTRY,
                    CtsModel.PROP_CORRESPONDENT_POSTCODE
            },
            ", "
    );


    static final Placeholder MEMBER_ADDRESS = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PROP_REPLY_TO_ADDRESS_LINE1,
                    CtsModel.PROP_REPLY_TO_ADDRESS_LINE2,
                    CtsModel.PROP_REPLY_TO_ADDRESS_LINE3,
                    CtsModel.PROP_REPLY_TO_COUNTRY,
                    CtsModel.PROP_REPLY_TO_POSTCODE
            },
            "\n"
    );

    static final Placeholder REPLY_TO_NAME = new PropertyPlaceholder(CtsModel.PROP_REPLY_TO_NAME);

    static final Placeholder REPLY_TO_ADDRESS = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PROP_REPLY_TO_ADDRESS_LINE1,
                    CtsModel.PROP_REPLY_TO_ADDRESS_LINE2,
                    CtsModel.PROP_REPLY_TO_ADDRESS_LINE3,
                    CtsModel.PROP_REPLY_TO_COUNTRY,
                    CtsModel.PROP_REPLY_TO_POSTCODE
            },
            "\n"
    );

    static final Placeholder CORRESPONDENT_TITLE = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_TITLE);
    static final Placeholder CORRESPONDENT_FORENAME = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_FORENAME);
    static final Placeholder CORRESPONDENT_SURNAME = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_SURNAME);

    static final Placeholder CORRESPONDENT_ADDRESS_LINE1 = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE1);
    static final Placeholder CORRESPONDENT_ADDRESS_LINE2 = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE2);
    static final Placeholder CORRESPONDENT_ADDRESS_LINE3 = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_ADDRESS_LINE3);
    static final Placeholder CORRESPONDENT_POSTCODE = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_POSTCODE);
    static final Placeholder MP_REF = new PropertyPlaceholder(CtsModel.PROP_MP_REF);
    static final Placeholder CORRESPONDENT_EMAIL = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENT_EMAIL);
    static final Placeholder DATE_OF_LETTER = new DatePropertyPlaceholder(CtsModel.PROP_DATE_OF_LETTER, new SimpleDateFormat("dd.MM.yyyy"));

    static final Placeholder CORRESPONDENCE_TYPE = new PropertyPlaceholder(CtsModel.PROP_CORRESPONDENCE_TYPE);
    static final Placeholder URN_SUFFIX = new PropertyPlaceholder(CtsModel.PROP_URN_SUFFIX);
    static final Placeholder MARKUP_MINISTER = new PropertyPlaceholder(CtsModel.PROP_MARKUP_MINISTER);

    static final Placeholder REPLY_TO_MEMBER = new PropertyPlaceholder(CtsModel.PROP_MEMBER);
    static final Placeholder CONSTITUENCY = new PropertyPlaceholder(CtsModel.PROP_CONSTITUENCY);
    static final Placeholder PQ_QUESTION = new PropertyPlaceholder(CtsModel.PROP_QUESTION_TEXT);
    static final Placeholder GROUPED_QUESTIONS = new GroupedQuestionsPlaceholder();
    static final Placeholder ANSWERING_MINISTER = new PropertyPlaceholder(CtsModel.PROP_ANSWERING_MINISTER);
    static final Placeholder PQ_ANSWER = new PropertyPlaceholder(CtsModel.PROP_ANSWER_TEXT);
    static final Placeholder UIN = new PropertyPlaceholder(CtsModel.PROP_UIN);
    static final Placeholder DEADLINE = new DatePropertyPlaceholder(CtsModel.PROP_CASE_RESPONSE_DEADLINE, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder DATE_TODAY = new StaticValuePlaceholder(new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date()));

    static final Placeholder CORRESPONDING_NAME = new PropertyPlaceholder(CtsModel.CORRESPONDING_NAME);
    static final Placeholder NUMBER_OF_CHILDREN = new PropertyPlaceholder(CtsModel.NUMBER_OF_CHILDREN);
    static final Placeholder COUNTRY_OF_DESTINATION = new PropertyPlaceholder(CtsModel.COUNTRY_OF_DESTINATION);
    static final Placeholder OTHER_COUNTRIES_TO_BE_VISITED = new PropertyPlaceholder(CtsModel.OTHER_COUNTRIES_TO_BE_VISITED);
    static final Placeholder COUNTRIES_TO_BE_TRAVELLED_THROUGH = new PropertyPlaceholder(CtsModel.COUNTRIES_TO_BE_TRAVELLED_THROUGH);
    static final Placeholder DEPARTURE_DATE_FROM_UK = new DatePropertyPlaceholder(CtsModel.DEPARTURE_DATE_FROM_UK, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder ARRIVING_DATE_IN_UK = new DatePropertyPlaceholder(CtsModel.ARRIVING_DATE_IN_UK, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder LEADERS_ADDRESS_ABOARD = new PropertyPlaceholder(CtsModel.LEADERS_ADDRESS_ABOARD);
    static final Placeholder PARTY_LEADER = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PARTY_LEADER_OTHER_NAMES,
                    CtsModel.PARTY_LEADER_LAST_NAME
            },
            " "
    );
    static final Placeholder PARTY_LEADER_OTHER_NAMES = new PropertyPlaceholder(CtsModel.PARTY_LEADER_OTHER_NAMES);
    static final Placeholder PARTY_LEADER_LAST_NAME = new PropertyPlaceholder(CtsModel.PARTY_LEADER_LAST_NAME);
    static final Placeholder PARTY_LEADER_PASSPORT_NUMBER = new PropertyPlaceholder(CtsModel.PARTY_LEADER_PASSPORT_NUMBER);
    static final Placeholder PARTY_LEADER_PASSPORT_ISSUED_AT = new PropertyPlaceholder(CtsModel.PARTY_LEADER_PASSPORT_ISSUED_AT);
    static final Placeholder PARTY_LEADER_PASSPORT_ISSUED_ON = new DatePropertyPlaceholder(CtsModel.PARTY_LEADER_PASSPORT_ISSUED_ON, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder PARTY_LEADER_DEPUTY = new MultiPropertyPlaceholder(
            new QName[]{
                    CtsModel.PARTY_LEADER_DEPUTY_OTHER_NAMES,
                    CtsModel.PARTY_LEADER_DEPUTY_LAST_NAME
            },
            " "
    );
    static final Placeholder PARTY_LEADER_DEPUTY_OTHER_NAMES = new PropertyPlaceholder(CtsModel.PARTY_LEADER_DEPUTY_OTHER_NAMES);
    static final Placeholder PARTY_LEADER_DEPUTY_LAST_NAME = new PropertyPlaceholder(CtsModel.PARTY_LEADER_DEPUTY_LAST_NAME);
    static final Placeholder PARTY_LEADER_DEPUTY_PASSPORT_NUMBER = new PropertyPlaceholder(CtsModel.PARTY_LEADER_DEPUTY_PASSPORT_NUMBER);
    static final Placeholder PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_AT = new PropertyPlaceholder(CtsModel.PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_AT);
    static final Placeholder PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_ON = new DatePropertyPlaceholder(CtsModel.PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_ON, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder DELIVERY_TYPE = new PropertyPlaceholder(CtsModel.DELIVERY_TYPE);
    static final Placeholder PASSPORT_STATUS = new PropertyPlaceholder(CtsModel.PASSPORT_STATUS);
    static final Placeholder BRING_UP_DATE = new DatePropertyPlaceholder(CtsModel.BRING_UP_DATE, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder DISPATCHED_DATE = new DatePropertyPlaceholder(CtsModel.DISPATCHED_DATE, new SimpleDateFormat("dd.MM.yyyy"));
    static final Placeholder DELIVERY_NUMBER = new PropertyPlaceholder(CtsModel.DELIVERY_NUMBER);

    static final Placeholder MEMBER = new PropertyPlaceholder(CtsModel.PROP_MEMBER);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_FORENAME = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_FORENAME);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_SURNAME = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_SURNAME);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_ORGANISATION = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ORGANISATION);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_TELEPHONE = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_TELEPHONE);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_EMAIL = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_EMAIL);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_POSTCODE = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_POSTCODE);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE1 = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE1);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE2 = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE2);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE3 = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE3);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_COUNTRY = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_COUNTRY);
    static final Placeholder THIRD_PARTY_CORRESPONDENT_TITLE = new PropertyPlaceholder(CtsModel.PROP_THIRD_PARTY_CORRESPONDENT_TITLE);
    static final Placeholder CASE_REF = new PropertyPlaceholder(CtsModel.PROP_CASE_REF);

    /**
     * Return a Map of all available placeholders.
     *
     * @return Map
     */
    public static Map<String, Placeholder> getAllPlaceholders() {
        Map<String, Placeholder> placeholders = new HashMap<>();
        placeholders.put("CORRESPONDENT_NAME", CORRESPONDENT_NAME);

        placeholders.put("CORRESPONDENT_TITLE", CORRESPONDENT_TITLE);
        placeholders.put("CORRESPONDENT_FORENAME", CORRESPONDENT_FORENAME);
        placeholders.put("CORRESPONDENT_SURNAME", CORRESPONDENT_SURNAME);

        placeholders.put("CORRESPONDENT_ADDRESS_LINE1", CORRESPONDENT_ADDRESS_LINE1);
        placeholders.put("CORRESPONDENT_ADDRESS_LINE2", CORRESPONDENT_ADDRESS_LINE2);
        placeholders.put("CORRESPONDENT_ADDRESS_LINE3", CORRESPONDENT_ADDRESS_LINE3);
        placeholders.put("CORRESPONDENT_POSTCODE", CORRESPONDENT_POSTCODE);

        placeholders.put("MP_REF", MP_REF);
        placeholders.put("CORRESPONDENT_EMAIL", CORRESPONDENT_EMAIL);
        placeholders.put("DATE_OF_LETTER", DATE_OF_LETTER);
        placeholders.put("URN_SUFFIX", URN_SUFFIX);
        placeholders.put("CORRESPONDENCE_TYPE", CORRESPONDENCE_TYPE);
        placeholders.put("MARKUP_MINISTER", MARKUP_MINISTER);

        placeholders.put("CORRESPONDENT_ADDRESS", CORRESPONDENT_ADDRESS);
        placeholders.put("CORRESPONDENT_ADDRESS_COMMA", CORRESPONDENT_ADDRESS_COMMA);
        placeholders.put("MEMBER_ADDRESS", MEMBER_ADDRESS);
        placeholders.put("REPLY_TO_NAME", REPLY_TO_NAME);
        placeholders.put("REPLY_TO_ADDRESS", REPLY_TO_ADDRESS);
        placeholders.put("PQ_QUESTION", PQ_QUESTION);
        placeholders.put("GROUPED_QUESTIONS", GROUPED_QUESTIONS);
        placeholders.put("PQ_ANSWER", PQ_ANSWER);
        placeholders.put("UIN", UIN);
        placeholders.put("REPLY_TO_MEMBER", REPLY_TO_MEMBER);
        placeholders.put("CONSTITUENCY", CONSTITUENCY);
        placeholders.put("ANSWERING_MINISTER", ANSWERING_MINISTER);
        placeholders.put("DEADLINE", DEADLINE);
        placeholders.put("DATE_TODAY", PlaceholderList.DateToday());

        placeholders.put("CORRESPONDING_NAME", CORRESPONDING_NAME);
        placeholders.put("NUMBER_OF_CHILDREN", NUMBER_OF_CHILDREN);
        placeholders.put("COUNTRY_OF_DESTINATION", COUNTRY_OF_DESTINATION);
        placeholders.put("OTHER_COUNTRIES_TO_BE_VISITED", OTHER_COUNTRIES_TO_BE_VISITED);
        placeholders.put("COUNTRIES_TO_BE_TRAVELLED_THROUGH", COUNTRIES_TO_BE_TRAVELLED_THROUGH);
        placeholders.put("DEPARTURE_DATE_FROM_UK", DEPARTURE_DATE_FROM_UK);
        placeholders.put("ARRIVING_DATE_IN_UK", ARRIVING_DATE_IN_UK);
        placeholders.put("LEADERS_ADDRESS_ABOARD", LEADERS_ADDRESS_ABOARD);
        placeholders.put("PARTY_LEADER", PARTY_LEADER);
        placeholders.put("PARTY_LEADER_LAST_NAME", PARTY_LEADER_LAST_NAME);
        placeholders.put("PARTY_LEADER_OTHER_NAMES", PARTY_LEADER_OTHER_NAMES);
        placeholders.put("PARTY_LEADER_PASSPORT_NUMBER", PARTY_LEADER_PASSPORT_NUMBER);
        placeholders.put("PARTY_LEADER_PASSPORT_ISSUED_AT", PARTY_LEADER_PASSPORT_ISSUED_AT);
        placeholders.put("PARTY_LEADER_PASSPORT_ISSUED_ON", PARTY_LEADER_PASSPORT_ISSUED_ON);
        placeholders.put("PARTY_LEADER_DEPUTY", PARTY_LEADER_DEPUTY);
        placeholders.put("PARTY_LEADER_DEPUTY_OTHER_NAMES", PARTY_LEADER_DEPUTY_OTHER_NAMES);
        placeholders.put("PARTY_LEADER_DEPUTY_LAST_NAME", PARTY_LEADER_DEPUTY_LAST_NAME);
        placeholders.put("PARTY_LEADER_DEPUTY_PASSPORT_NUMBER", PARTY_LEADER_DEPUTY_PASSPORT_NUMBER);
        placeholders.put("PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_AT", PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_AT);
        placeholders.put("PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_ON", PARTY_LEADER_DEPUTY_PASSPORT_ISSUED_ON);
        placeholders.put("DELIVERY_TYPE", DELIVERY_TYPE);
        placeholders.put("BRING_UP_DATE", BRING_UP_DATE);
        placeholders.put("DISPATCHED_DATE", DISPATCHED_DATE);
        placeholders.put("DELIVERY_NUMBER", DELIVERY_NUMBER);

        placeholders.put("MEMBER", MEMBER);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_FORENAME", THIRD_PARTY_CORRESPONDENT_FORENAME);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_FORNAME", THIRD_PARTY_CORRESPONDENT_FORENAME);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_SURNAME", THIRD_PARTY_CORRESPONDENT_SURNAME);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_ORGANISATION", THIRD_PARTY_CORRESPONDENT_ORGANISATION);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_TELEPHONE", THIRD_PARTY_CORRESPONDENT_TELEPHONE);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_EMAIL", THIRD_PARTY_CORRESPONDENT_EMAIL);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_POSTCODE", THIRD_PARTY_CORRESPONDENT_POSTCODE);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE1", THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE1);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE2", THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE2);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE3", THIRD_PARTY_CORRESPONDENT_ADDRESS_LINE3);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_COUNTRY", THIRD_PARTY_CORRESPONDENT_COUNTRY);
        placeholders.put("THIRD_PARTY_CORRESPONDENT_TITLE", THIRD_PARTY_CORRESPONDENT_TITLE);
        placeholders.put("CASE_REF",CASE_REF);
        return placeholders;
    }

    public static Placeholder DateToday() {
        return new StaticValuePlaceholder(new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date()));
    }


}

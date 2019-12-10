package me.umov.shared.libraries.domain.utils

import spock.lang.Specification

class TitleTest extends Specification {

    def "Should return title when title is even"() {
        given:
            String title = "The title length is even"

        when:
            String result = Title.buildTitle(title)

        then:
            result == "----------------------------- The title length is even -----------------------------"
    }

    def "Should return title when title is odd"() {
        given:
            String title = "The title length is odd"

        when:
            String result = Title.buildTitle(title)

        then:
            result == "----------------------------- The title length is odd ------------------------------"
    }

    def "Should return null when receive null in title"() {
        given:
            String title = null

        when:
            String result = Title.buildTitle(title)

        then:
            result == null
    }

    def "Should return title itself when title length is equals to max title length"() {
        given:
            String title = "The title have the max length, The title have the max length, The title have the max"

        when:
            String result = Title.buildTitle(title)

        then:
            result == "The title have the max length, The title have the max length, The title have the max"
    }

    def "Should return title itself when title length is bigger than max title length"() {
        given:
            String title = "The title is bigger than max length, The title is bigger than max length, The title is bigger than max length"

        when:
            String result = Title.buildTitle(title)

        then:
            result == "The title is bigger than max length, The title is bigger than max length, The title is bigger than max length"
    }

}

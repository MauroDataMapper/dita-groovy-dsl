package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.enums.Importance
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Status

trait MetadataAttributeGroup {

    List<String> propsText = []
    List<String> baseText = []

    List<String> platformText = []
    List<String> productText = []
    List<String> audienceText = []
    List<String> otherPropsText = []

    String deliveryTargetText
    Importance importanceText
    String revText
    Status statusText

    def props(String props) {
        propsText << props
    }

    def props(List<String> props) {
        propsText.addAll(props)
    }

    def base(String base) {
        baseText << base
    }

    def base(List<String> base) {
        baseText.addAll(base)
    }

    def platform(String platform) {
        platformText << platform
    }

    def platform(List<String> platform) {
        platformText.addAll(platform)
    }

    def product(String product) {
        productText << product
    }

    def product(List<String> product) {
        productText.addAll(product)
    }

    def audience(String audience) {
        audienceText << audience
    }

    def audience(List<String> audience) {
        audienceText.addAll(audience)
    }

    def otherProps(String otherProps) {
        otherPropsText << otherProps
    }

    def otherProps(List<String> otherProps) {
        otherPropsText.addAll(otherProps)
    }

    def deliveryTarget(String deliveryTarget) {
        this.deliveryTargetText = deliveryTarget
    }

    def importance(Importance importance) {
        this.importanceText = importance
    }

    def rev(String rev) {
        this.revText = rev
    }

    def status(Status status) {
        this.statusText = status
    }



}

package uk.ac.ox.softeng.maurodatamapper.dita.attributes

import uk.ac.ox.softeng.maurodatamapper.dita.meta.AttributeGroup
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Importance
import uk.ac.ox.softeng.maurodatamapper.dita.enums.Status
import uk.ac.ox.softeng.maurodatamapper.dita.meta.SpaceSeparatedStringList

trait MetadataAttributeGroup implements AttributeGroup {

	SpaceSeparatedStringList props = []
	SpaceSeparatedStringList base = []
	SpaceSeparatedStringList platform = []
	SpaceSeparatedStringList product = []
	SpaceSeparatedStringList audience = []
	SpaceSeparatedStringList otherProps = []
	String deliveryTarget
	Importance importance
	String rev
	Status status

	Map attributeMap() {
		return [
			"props": props,
			"base": base,
			"platform": platform,
			"product": product,
			"audience": audience,
			"otherProps": otherProps,
			"deliveryTarget": deliveryTarget,
			"importance": importance,
			"rev": rev,
			"status": status,
		]
	}
	void props(SpaceSeparatedStringList props) {
		this.props = props
	}

	void props(Collection<String> props) {
		this.props = new SpaceSeparatedStringList(props)
	}

	void props(String props) {
		this.props = new SpaceSeparatedStringList(props.split(" ") as List)
	}

	void base(SpaceSeparatedStringList base) {
		this.base = base
	}

	void base(Collection<String> base) {
		this.base = new SpaceSeparatedStringList(base)
	}

	void base(String base) {
		this.base = new SpaceSeparatedStringList(base.split(" ") as List)
	}

	void platform(SpaceSeparatedStringList platform) {
		this.platform = platform
	}

	void platform(Collection<String> platform) {
		this.platform = new SpaceSeparatedStringList(platform)
	}

	void platform(String platform) {
		this.platform = new SpaceSeparatedStringList(platform.split(" ") as List)
	}

	void product(SpaceSeparatedStringList product) {
		this.product = product
	}

	void product(Collection<String> product) {
		this.product = new SpaceSeparatedStringList(product)
	}

	void product(String product) {
		this.product = new SpaceSeparatedStringList(product.split(" ") as List)
	}

	void audience(SpaceSeparatedStringList audience) {
		this.audience = audience
	}

	void audience(Collection<String> audience) {
		this.audience = new SpaceSeparatedStringList(audience)
	}

	void audience(String audience) {
		this.audience = new SpaceSeparatedStringList(audience.split(" ") as List)
	}

	void otherProps(SpaceSeparatedStringList otherProps) {
		this.otherProps = otherProps
	}

	void otherProps(Collection<String> otherProps) {
		this.otherProps = new SpaceSeparatedStringList(otherProps)
	}

	void otherProps(String otherProps) {
		this.otherProps = new SpaceSeparatedStringList(otherProps.split(" ") as List)
	}

	void deliveryTarget(String deliveryTarget) {
		this.deliveryTarget = deliveryTarget
	}

	void importance(Importance importance) {
		this.importance = importance
	}

	void rev(String rev) {
		this.rev = rev
	}

	void status(Status status) {
		this.status = status
	}

	@Override
	List<String> validate() {
		return []
	}
}

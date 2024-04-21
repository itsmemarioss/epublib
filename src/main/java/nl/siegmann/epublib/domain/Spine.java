package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The spine sections are the sections of the book in the order in which the book should be read.
 * <p>
 * This contrasts with the Table of Contents sections which is an index into the Book's sections.
 *
 * @author paul
 * @see nl.siegmann.epublib.domain.TableOfContents
 */
public class Spine implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3878483958947357246L;
	private String id;
	private PageProgressionDirection direction;
	private Resource tocResource;
	private List<SpineReference> spineReferences;

	public Spine() {
		this(new ArrayList<SpineReference>());
	}

	/**
	 * Creates a spine out of all the resources in the table of contents.
	 *
	 * @param tableOfContents
	 */
	public Spine(TableOfContents tableOfContents) {
		this.spineReferences = createSpineReferences(tableOfContents.getAllUniqueResources());
	}

	public Spine(List<SpineReference> spineReferences) {
		this.spineReferences = spineReferences;
	}

	public static List<SpineReference> createSpineReferences(Collection<Resource> resources) {
		List<SpineReference> result = new ArrayList<SpineReference>(resources.size());
		for (Resource resource : resources) {
			result.add(new SpineReference(resource));
		}
		return result;
	}

	public List<SpineReference> getSpineReferences() {
		return spineReferences;
	}

	public void setSpineReferences(List<SpineReference> spineReferences) {
		this.spineReferences = spineReferences;
	}

	/**
	 * Gets the resource at the given index. Null if not found.
	 *
	 * @param index
	 * @return the resource at the given index.
	 */
	public Resource getResource(int index) {
		if (index < 0 || index >= spineReferences.size()) {
			return null;
		}
		return spineReferences.get(index).getResource();
	}

	/**
	 * Finds the first resource that has the given resourceId.
	 * <p>
	 * Null if not found.
	 *
	 * @param resourceId
	 * @return the first resource that has the given resourceId.
	 */
	public int findFirstResourceById(String resourceId) {
		if (StringUtil.isBlank(resourceId)) {
			return -1;
		}

		for (int i = 0; i < spineReferences.size(); i++) {
			SpineReference spineReference = spineReferences.get(i);
			if (resourceId.equals(spineReference.getResourceId())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Adds the given spineReference to the spine references and returns it.
	 *
	 * @param spineReference
	 * @return the given spineReference
	 */
	public SpineReference addSpineReference(SpineReference spineReference) {
		if (spineReferences == null) {
			this.spineReferences = new ArrayList<SpineReference>();
		}
		spineReferences.add(spineReference);
		return spineReference;
	}

	/**
	 * Adds the given spineReference to the spine references and returns it.
	 *
	 * @param spineReference
	 * @param resourceId     the id of the resource preceding the new resource to be inserted.
	 * @return the given spineReference or null if the resourceId didn't exist.
	 */
	public SpineReference addSpineReference(SpineReference spineReference, String resourceId) {
		if (spineReferences == null) {
			this.spineReferences = new ArrayList<SpineReference>();
		}
		int idx = getResourceIndexWithId(resourceId);
		if (idx > -1) {
			spineReferences.add(idx + 1, spineReference);
			return spineReference;
		} else {
			return null;
		}
	}

	/**
	 * Adds the given spineReferences to the spine references and returns it.
	 *
	 * @param spineReferences references to be added.
	 * @param resourceId      the id of the resource preceding the new resources to be inserted.
	 * @return the last spineReference inserted or null if the resourceId didn't exist or there was no spineReference to insert..
	 */
	public SpineReference addSpineReferences(List<SpineReference> spineReferences, String resourceId) {
		int precedingResidx = getResourceIndexWithId(resourceId);
		if (precedingResidx == -1) {
			return null;
		}

		int idx = 0;
		SpineReference sr = null;
		String previousResId = resourceId;
		while (idx < spineReferences.size()) {
			sr = spineReferences.get(idx);
			addSpineReference(sr, previousResId);
			previousResId = sr.getResourceId();
			idx++;
		}
		return sr;
	}

	/**
	 * Adds the given resource to the spine references and returns it.
	 *
	 * @return the given spineReference
	 */
	public SpineReference addResource(Resource resource) {
		return addSpineReference(new SpineReference(resource));
	}

	/**
	 * The number of elements in the spine.
	 *
	 * @return The number of elements in the spine.
	 */
	public int size() {
		return spineReferences.size();
	}

	/**
	 * As per the epub file format the spine officially maintains a reference to the Table of Contents. The epubwriter
	 * will look for it here first, followed by some clever tricks to find it elsewhere if not found. Put it here to be
	 * sure of the expected behaviours.
	 *
	 * @param tocResource
	 */
	public void setTocResource(Resource tocResource) {
		this.tocResource = tocResource;
	}

	/**
	 * The resource containing the XML for the tableOfContents. When saving an epub file this resource needs to be in
	 * this place.
	 *
	 * @return The resource containing the XML for the tableOfContents.
	 */
	public Resource getTocResource() {
		return tocResource;
	}

	/**
	 * The position within the spine of the given resource.
	 *
	 * @param currentResource
	 * @return something &lt; 0 if not found.
	 */
	public int getResourceIndex(Resource currentResource) {
		if (currentResource == null) {
			return -1;
		}
		return getResourceIndex(currentResource.getHref());
	}

	/**
	 * The first position within the spine of a resource with the given href.
	 *
	 * @return something &lt; -1 if not found.
	 */
	public int getResourceIndex(String resourceHref) {
		int result = -1;
		if (StringUtil.isBlank(resourceHref)) {
			return result;
		}
		for (int i = 0; i < spineReferences.size(); i++) {
			if (resourceHref.equals(spineReferences.get(i).getResource().getHref())) {
				result = i;
				break;
			}
		}
		return result;
	}

	/**
	 * The first position within the spine of a resource with the given id.
	 *
	 * @return something &lt; -1 if not found.
	 */
	public int getResourceIndexWithId(String resourceId) {
		int result = -1;
		for (int i = 0; i < spineReferences.size(); i++) {
			if (resourceId.equals(spineReferences.get(i).getResource().getId())) {
				result = i;
				break;
			}
		}
		return result;
	}

	/**
	 * Whether the spine has any references
	 *
	 * @return Whether the spine has any references
	 */
	public boolean isEmpty() {
		return spineReferences.isEmpty();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PageProgressionDirection getDirection() {
		return direction;
	}

	public void setDirection(PageProgressionDirection direction) {
		this.direction = direction;
	}
}

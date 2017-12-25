package nl.siegmann.epublib.domain;

import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.StringUtil;

import java.io.Serializable;
import java.util.*;

/**
 * All the resources that make up the book. XHTML files, images and epub xml documents must be here.
 * @author paul
 */
public class Resources implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2450876953383871451L;
    private static final String IMAGE_PREFIX = "image_";
    private static final String ITEM_PREFIX = "item_";
    private int lastId = 1;

    private Map<String, Resource> resourcesByHref = new HashMap<String, Resource>();
    private Map<String, Resource> resourcesById = new HashMap<String, Resource>();

    /**
     * Adds a resource to the resources.
     * <p>
     * Fixes the resources id and href if necessary.
     * @param resource
     * @return the newly added resource
     */
    public Resource add(Resource resource) {
        fixResourceHref(resource);
        fixResourceId(resource);
        this.resourcesByHref.put(resource.getHref(), resource);
        this.resourcesById.put(resource.getId(), resource);
        return resource;
    }

    /**
     * Checks the id of the given resource and changes to a unique identifier if it isn't one already.
     * @param resource
     */
    public void fixResourceId(Resource resource) {
        String resourceId = resource.getId();

        // first try and create a unique id based on the resource's href
        if (StringUtil.isBlank(resource.getId())) {
            resourceId = StringUtil.substringBeforeLast(resource.getHref(), '.');
            resourceId = StringUtil.substringAfterLast(resourceId, '/');
        }

        resourceId = makeValidId(resourceId, resource);

        // check if the id is unique. if not: create one from scratch
        if (StringUtil.isBlank(resourceId) || containsId(resourceId)) {
            resourceId = createUniqueResourceId(resource);
        }
        resource.setId(resourceId);
    }

    /**
     * Check if the id is a valid identifier. if not: prepend with valid identifier
     * @param resource
     * @return a valid id
     */
    private String makeValidId(String resourceId, Resource resource) {
        if (StringUtil.isNotBlank(resourceId) && !Character.isJavaIdentifierStart(resourceId.charAt(0))) {
            resourceId = getResourceItemPrefix(resource) + resourceId;
        }
        return resourceId;
    }

    private String getResourceItemPrefix(Resource resource) {
        String result;
        if (MediatypeService.isBitmapImage(resource.getMediaTypeProperty())) {
            result = IMAGE_PREFIX;
        } else {
            result = ITEM_PREFIX;
        }
        return result;
    }

    /**
     * Creates a new resource id that is guaranteed to be unique for this set of Resources
     * @param resource
     * @return a new resource id that is guaranteed to be unique for this set of Resources
     */
    private String createUniqueResourceId(Resource resource) {
        int counter = lastId;
        if (counter == Integer.MAX_VALUE) {
            if (resourcesByHref.size() == Integer.MAX_VALUE) {
                throw new IllegalArgumentException(
                        "Resources contains " + Integer.MAX_VALUE + " elements: no new elements can be added");
            } else {
                counter = 1;
            }
        }
        String prefix = getResourceItemPrefix(resource);
        String result = prefix + counter;
        while (containsId(result)) {
            result = prefix + (++counter);
        }
        lastId = counter;
        return result;
    }

    /**
     * Whether the map of resources already contains a resource with the given id.
     * @param id
     * @return Whether the map of resources already contains a resource with the given id.
     */
    public boolean containsId(String id) {
        return resourcesById.containsKey(id);
    }

    /**
     * Gets the resource with the given id.
     * @param id
     * @return null if not found
     */
    public Resource getById(String id) {
        return resourcesById.get(id);
    }

    /**
     * Remove the resource with the given href.
     * @param href
     * @return the removed resource, null if not found
     */
    public Resource remove(String href) {
        Resource resource = resourcesByHref.remove(href);
        if (resource != null) {
            resourcesById.remove(resource.getId());
        }
        return resource;
    }

    private void fixResourceHref(Resource resource) {
        if (StringUtil.isNotBlank(resource.getHref())
                && !resourcesByHref.containsKey(resource.getHref())) {
            return;
        }
        if (StringUtil.isBlank(resource.getHref())) {
            if (resource.getMediaTypeProperty() == null) {
                throw new IllegalArgumentException("Resource must have either a MediaTypeProperties or a href");
            }
            int i = 1;
            String href = createHref(resource.getMediaTypeProperty(), i);
            while (resourcesByHref.containsKey(href)) {
                href = createHref(resource.getMediaTypeProperty(), (++i));
            }
            resource.setHref(href);
        }
    }

    private String createHref(MediaTypeProperty mediaTypeProperty, int counter) {
        if (MediatypeService.isBitmapImage(mediaTypeProperty)) {
            return "image_" + counter + mediaTypeProperty.getDefaultExtension();
        } else {
            return "item_" + counter + mediaTypeProperty.getDefaultExtension();
        }
    }


    public boolean isEmpty() {
        return resourcesByHref.isEmpty();
    }

    /**
     * The number of resources
     * @return The number of resources
     */
    public int size() {
        return resourcesByHref.size();
    }

    /**
     * The resources that make up this book. Resources can be xhtml pages, images, xml documents, etc.
     * @return The resources that make up this book.
     */
    public Map<String, Resource> getResourceMap() {
        return resourcesByHref;
    }

    public Collection<Resource> getAll() {
        return resourcesByHref.values();
    }


    /**
     * Whether there exists a resource with the given href
     * @param href
     * @return Whether there exists a resource with the given href
     */
    public boolean containsByHref(String href) {
        if (StringUtil.isBlank(href)) {
            return false;
        }
        return resourcesByHref.containsKey(StringUtil.substringBefore(href, Constants.FRAGMENT_SEPARATOR_CHAR));
    }

    /**
     * Sets the collection of Resources to the given collection of resources
     * @param resources
     */
    public void set(Collection<Resource> resources) {
        this.resourcesByHref.clear();
        addAll(resources);
    }

    /**
     * Adds all resources from the given Collection of resources to the existing collection.
     * @param resources
     */
    public void addAll(Collection<Resource> resources) {
        for (Resource resource : resources) {
            add(resource);
        }
    }

    /**
     * Sets the collection of Resources to the given collection of resources
     * @param resources A map with as keys the resources href and as values the Resources
     */
    public void set(Map<String, Resource> resources) {
        this.resourcesByHref = new HashMap<String, Resource>(resources);
    }


    /**
     * First tries to find a resource with as id the given idOrHref, if that fails it tries to find one with the
     * idOrHref as href.
     * @param idOrHref
     * @return the found Resource
     */
    public Resource getByIdOrHref(String idOrHref) {
        Resource resource = getById(idOrHref);
        if (resource == null) {
            resource = getByHref(idOrHref);
        }
        return resource;
    }


    /**
     * Gets the resource with the given href. If the given href contains a fragmentId then that fragment id will be
     * ignored.
     * @param href
     * @return null if not found.
     */
    public Resource getByHref(String href) {
        if (StringUtil.isBlank(href)) {
            return null;
        }
        href = StringUtil.substringBefore(href, Constants.FRAGMENT_SEPARATOR_CHAR);
        Resource result = resourcesByHref.get(href);
        return result;
    }

    /**
     * Gets the first resource (random order) with the give mediatype.
     * <p>
     * Useful for looking up the table of contents as it's supposed to be the only resource with NCX mediatype.
     * @param mediaTypeProperty
     * @return
     */
    public Resource findFirstResourceByMediaType(MediaTypeProperty mediaTypeProperty) {
        return findFirstResourceByMediaType(resourcesByHref.values(), mediaTypeProperty);
    }

    /**
     * Gets the first resource (random order) with the give mediatype.
     * <p>
     * Useful for looking up the table of contents as it's supposed to be the only resource with NCX mediatype.
     * @param mediaTypeProperty
     * @return
     */
    public static Resource findFirstResourceByMediaType(Collection<Resource> resources, MediaTypeProperty
            mediaTypeProperty) {
        for (Resource resource : resources) {
            MediaTypeProperty resourceMtp = resource.getMediaTypeProperty();
            if (resourceMtp != null && resourceMtp.equals(mediaTypeProperty)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * All resources that have the given MediaType.
     * @param mediaTypeProperty
     * @return
     */
    public List<Resource> getResourcesByMediaType(MediaTypeProperty mediaTypeProperty) {
        List<Resource> result = new ArrayList<Resource>();
        if (mediaTypeProperty == null) {
            return result;
        }
        for (Resource resource : getAll()) {
            MediaTypeProperty rMtp = resource.getMediaTypeProperty();
            if (rMtp != null && rMtp.equals(mediaTypeProperty)) {
                result.add(resource);
            }
        }
        return result;
    }

    /**
     * All Resources that match any of the given list of MediaTypes
     * @param mediaTypeProperties
     * @return
     */
    public List<Resource> getResourcesByMediaTypes(MediaTypeProperty[] mediaTypeProperties) {
        List<Resource> result = new ArrayList<>();
        if (mediaTypeProperties == null) {
            return result;
        }

        // this is the fastest way of doing this according to
        // http://stackoverflow.com/questions/1128723/in-java-how-can-i-test-if-an-array-contains-a-certain-value
        List<MediaTypeProperty> mediaTypesListProperties = Arrays.asList(mediaTypeProperties);
        for (Resource resource : getAll()) {
            if (mediaTypesListProperties.contains(resource.getMediaTypeProperty())) {
                result.add(resource);
            }
        }
        return result;
    }


    /**
     * All resource hrefs
     * @return all resource hrefs
     */
    public Collection<String> getAllHrefs() {
        return resourcesByHref.keySet();
    }
}

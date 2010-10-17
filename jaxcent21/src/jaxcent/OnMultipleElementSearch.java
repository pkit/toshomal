package jaxcent;

public interface OnMultipleElementSearch {
    public void onSearchComplete( HtmlElement[] elements, String[] attributeValues );
    public void onTimeout();
}

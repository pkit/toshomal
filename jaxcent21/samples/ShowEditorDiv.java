package sample;

import jaxcent.*;
import toshomal.DbShow;

import java.util.HashMap;

public class ShowEditorDiv extends HtmlDiv
{
    private JaxcentPage tpage;
    private DbShow show;
    private static final String[] statusList =
            {
                    "Select status",
                    "Currently Airing",
                    "Finished Airing",
                    "Not yet aired",
                    "Unknown"
            };
    private static final String[] typeList =
            {
                    "Select type",
                    "TV",
                    "Movie",
                    "OVA",
                    "Special",
                    "Unknown"
            };

    public ShowEditorDiv(JaxcentPage page, SearchType searchType, String text, String[] attributes, String[] values, DbShow show) throws Jaxception {
        super(page, searchType, text, attributes, values);
        this.tpage = page;
        this.show = show;

        HtmlForm form = new HtmlForm(tpage, SearchType.createNew,
                new String[] { "method", "name", "id" },
                new String[] { "post", "EditShowForm", "showForm" }
        );

        HtmlTable tbl = new HtmlTable(tpage, SearchType.createNew,
                new String[] { "class", "border", "cellpadding", "cellspacing", "width" },
                new String[] { "light_bg", "0", "5", "0", "50%" }
        );

        tbl.insertRow(-1,
                new String[] {
                        "Anime Title",
                        "<input type=\"text\" name=\"show_name\" id=\"input_show_name\""
                                + " class=\"inputtext\" value=\""
                                + show.getName()
                                + "\" size=\"40\">"
                },
                new String[][] {
                        { "width", "class", "valign" },
                        { "class" }
                },
                new String[][] {
                        { "130", "borderClass", "top" },
                        { "borderClass" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "MAL ID",
                        "<input type=\"text\" name=\"show_mal_id\" id=\"input_mal_id\" class=\"inputtext\" value=\"" +
                                show.getMalId() +
                                "\" size=\"6\">"
                },
                new String[][] {
                        { "class" },
                        { "class" }
                },
                new String[][] {
                        { "borderClass" },
                        { "borderClass" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "Status",
                        "<select name=\"show_status\" id=\"input_show_status\" class=\"inputtext\">" +
                                buildOptionList(statusList, show.getStatus()) +
                                "</select>"
                },
                new String[][] {
                        { "class" },
                        { "class" }
                },
                new String[][] {
                        { "borderClass" },
                        { "borderClass" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "Type",
                        "<select name=\"show_type\" id=\"input_show_type\" class=\"inputtext\">" +
                                buildOptionList(typeList, show.getType()) +
                                "</select>"
                },
                new String[][] {
                        { "class" },
                        { "class" }
                },
                new String[][] {
                        { "borderClass" },
                        { "borderClass" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "Number of Episodes",
                        "<input type=\"text\" name=\"show_eps\" id=\"input_show_epsnum\" class=\"inputtext\" value=\"" +
                                String.format("%d", show.getEpsNum()) +
                                "\" size=\"3\">"
                },
                new String[][] {
                        { "class", "valign" },
                        { "class" }
                },
                new String[][] {
                        { "borderClass", "top" },
                        { "borderClass" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "Anime Image",
                        "<input type=\"text\" name=\"show_img\" id=\"input_show_img\" class=\"inputtext\" value=\"" +
                                show.getImgUrl() +
                                "\" size=\"55\"><div class=\"picSurround\"><img src=\"" +
                                show.getImgUrl() +
                                "\" border=\"0\"></a></div>"
                },
                new String[][] {
                        { "class", "valign" },
                        { "class" }
                },
                new String[][] {
                        { "borderClass", "center" },
                        { "borderClass" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "<input type=\"button\" id=\"ShowEditSubmitB\" class=\"inputButton\" " +
                                "style=\"font-weight: bold; font-size: 12px;\" " +
                                "value=\"Update & Close\">",
                },
                new String[][] {
                        { "colspan", "align" }
                },
                new String[][] {
                        { "2", "left" }
                }
        );

        tbl.insertRow(-1,
                new String[] {
                        "Search String",
                        "<input type=\"text\" name=\"show_search\" id=\"input_show_search\" class=\"inputtext\" value=\"" +
                                show.getName() +
                                "\" size=\"50\">" +
                        "<input type=\"button\" id=\"ShowEditSearch\" class=\"inputButton\" value=\"Search\">"
                },
                new String[][] {
                        { "class", "valign" },
                        { "class" }
                },
                new String[][] {
                        { "borderClass", "top" },
                        { "borderClass" }
                }
        );


        HtmlDiv footer = new HtmlDiv(tpage, SearchType.createNew,
                new String[] { "class", "style" },
                new String[] { "borderClass", "clear: both;" }
        );
        tbl.insertAtEnd(form);
        form.insertAtEnd(this);
        footer.insertAtEnd(this);
    }

    private String buildOptionList(String[] list, String status)
    {
        String result = "";
        for(int i = 0; i < list.length; ++i)
        {
            String selected = "";
            if (status.equals(list[i]))
                selected = " selected";
            result += String.format("<option value=\"%d\"%s>%s", i, selected, list[i]);
        }
        return result;
    }
}

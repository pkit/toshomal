// Tigra Menu Builder project file
// Project Name: undefined
// Saved: 02/19/2008

var N_MODE = 0;

var N_TPL = 1;

var A_MENU = {
	'menu_pos': 'relative',
	'menu_left': 50,
	'menu_top': 20
};

var A_TPL = [
	{
		'level_layout': 'h',
		'level_width': 100,
		'level_height': 24,
		'level_left': 99,
		'level_top': 0,
		'time_expand': 200,
		'time_hide': 200,
		'level_style': 0
	},
	{
		'level_layout': 'v',
		'level_width': 120,
		'level_btop': 25,
		'level_bleft': 0,
		'level_left': 0,
		'level_top': 23,
		'level_style': 1
	},
	{
		'level_btop': 10,
		'level_bleft': 105
	}
];

var A_ITEMS = [
	{
		'text_caption': 'Home',
		'children': [
			{
				'text_caption': 'Test',
				'link_href': 'javascript:JaxcentServerRequest(\'Test\');'
			}
		]
	},
	{
		'text_caption': 'Products',
		'children': [
			{
				'text_caption': 'Product Test',
				'link_href': 'javascript:JaxcentServerRequest(\'producttest\')'
			}
		]
	},
	{
		'text_caption': 'Services'
	},
	{
		'text_caption': 'Support'
	}
];

var A_STYLES = [
	{
		'name': 'blue grades - root level',
		'box_background_color': ['#3C76B2','#4D99E6'],
		'box_border_color': '#2B547F',
		'box_border_width': 1,
		'box_padding': 4,
		'font_color': '#FFFFFF',
		'font_family': 'Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;',
		'font_size': 12,
		'font_weight': 0,
		'font_style': 0,
		'font_decoration': 0,
		'text_align': 'center',
		'text_valign': 'middle',
		'n_order': 0,
		'n_id': 0,
		'classes_i': ['TM0i0','TM0i0','TM0i0'],
		'classes_o': ['TM0o0','TM0o1','TM0o1']
	},
	{
		'name': 'blue grades - sub levels',
		'box_background_color': ['#4D99E6','#3C76B2',null],
		'box_border_color': '#2B547F',
		'box_border_width': 1,
		'box_padding': 4,
		'font_color': '#FFFFFF',
		'font_family': 'Tahoma, Verdana, Geneva, Arial, Helvetica, sans-serif;',
		'font_size': 12,
		'font_weight': 0,
		'font_style': 0,
		'font_decoration': 0,
		'text_align': 'left',
		'text_valign': 'middle',
		'n_order': 1,
		'n_id': 1,
		'classes_i': ['TM0i0','TM0i0','TM0i0'],
		'classes_o': ['TM1o0','TM1o1','TM1o1']
	}
];


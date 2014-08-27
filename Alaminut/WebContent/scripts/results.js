$( window ).load(function(event) {
	var $currRecipe = $('#big-recipe');
	var $firstRecipe = $('.small-recipe').first();

	$currRecipe
		.children('img')
		.attr('src', $firstRecipe.children('img').attr('src'));

	$currRecipe
		.children('h2')
		.html($firstRecipe.children('h5').html());

	$currRecipe
		.children('p')
		.html($firstRecipe.children('p').html());

	$currRecipe
		.children('ul')
		.html($firstRecipe.children('ul').html());
});

$('#srecipe-container').delegate('.small-recipe', 'click', function(event) {
	var $currRecipe = $('#big-recipe');

	$currRecipe
		.children('img')
		.attr('src', $(this).children('img').attr('src'));

	$currRecipe
		.children('h2')
		.html($(this).children('h5').html());

	$currRecipe
		.children('p')
		.html($(this).children('p').html());

	$currRecipe
		.children('ul')
		.html($(this).children('ul').html());
});
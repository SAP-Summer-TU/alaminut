var inputCount = 2;

$('#add-button').click(function(event) {
	event.preventDefault();

	var $input = $('<input \>');
	$input.attr({
		type: 'text',
		id: 'product-' + inputCount
	});

	var $removeButton = $('<button \>');
	$removeButton.attr('value', 'product-' + inputCount);
	$removeButton.append('-');
	$removeButton.click(function(event) {
		event.preventDefault();

		$('#'+ this.value).remove();
		$(this).remove();
	});

	$('#submit-button').before($input);
	$('#submit-button').before($removeButton);

	inputCount++;
});	
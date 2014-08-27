var inputCount = 2;

$('#add-button').click(function(event) {
	event.preventDefault();

	var $divContainer = $('<div \>');
	$divContainer.attr('class', 'input-container');
	
	var $input = $('<input \>');
	$input.attr({
		type: 'text',
		name: 'product',
		class: 'product-input',
		id: 'product-' + inputCount
	});

	var $removeButton = $('<button \>');
	$removeButton.attr('value', 'product-' + inputCount);
	$removeButton.attr('class', 'remove-button');
	$removeButton.append('-');
	$removeButton.click(function(event) {
		event.preventDefault();

		$('#'+ this.value).parent().remove();
	});

	$('#add-button').before($divContainer);
	$divContainer.append($input);
	$divContainer.append($removeButton);

	//$('#add-button').before($input);
	//$('#add-button').before($removeButton);

	inputCount++;
});	

$('#form-container').on('change', '.product-input', function(event) {

	$(this).parent().children('select').remove();

	if($(this).val().length >= 3){
		$.post('AlaminutServlet', 
			{
				action: 'producs',
				productInput: $(this).val()
			}, 
			function(data) {
			
			

		});
	}
	/* Act on the event */
});
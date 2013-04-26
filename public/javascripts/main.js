//$('.fileupload').fileupload();

//if (window.FileReader) {
//    //then your code goes here
//} else {
//    alert('This browser does not support FileReader');
//}

function handleFileSelect(evt) {
    var files = evt.target.files;
    var f = files[0];
    var reader = new FileReader();

    reader.onload = (function(theFile) {
        return function(e) {
            document.getElementById('list').innerHTML = ['<img src="', e.target.result,'" title="', theFile.name, '" width="50" />'].join('');
        };
    })(f);

    reader.readAsDataURL(f);
}



//jQuery(document).ready(function($) {
    // Code using $ as usual goes here.
document.getElementById('file').addEventListener('change', handleFileSelect, false);
//});
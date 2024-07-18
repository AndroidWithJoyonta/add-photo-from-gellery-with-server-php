<?php

$image64 = $_POST['image'];
$decodedImage = base64_decode($image64);


$fileName = time(). '_' .rand(1000,100000) . '.jpg';
$filePath = 'images/'.$fileName;

if(file_put_contents($filePath,$decodedImage)){
    echo'Image Upload Successful';
}else{
    echo'Image Upload Failed';
}


?>
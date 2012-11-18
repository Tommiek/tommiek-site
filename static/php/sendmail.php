<?
$name    = $_POST['inputname'];
$email   = $_POST['inputmail'];
$phone   = $_POST['inputphone'];
$message = $_POST['inputmessage'] ;

if(strlen($name) < 1 or strlen($name) > 60){
  print('Input validatie : foutieve naam : Neem contact op met info@tommiek.nl');
  exit;
}

if(strlen($email) > 60){
  print('-'. $email . '-');
  print('Input validatie : foutieve email : Neem contact op met info@tommiek.nl');
  exit;
}


if(strlen($phone) > 60){
  print('-'. $phone . '-');
  print('Input validatie : foutieve telefoon : Neem contact op met info@tommiek.nl');
  exit;
}


if(strlen($message) > 2048){
  print('Input validatie : foutieve opmerkingen : Neem contact op met info@tommiek.nl');
  exit;
}

if(!mail( "tom.kloppenburg@gmail.com", "Tommiek.nl reactie van " . $name, "Naam\t: " . $name . "\nEmail\t: " . $email . "\nPhone\t: " . $phone . "\n\n" . $message, "From: info@tommiek.nl" )) {
  print("Mail versturen mislukt : Neem contact op met info@tommiek.nl");
} else {
  header( "Location: ../bedankt.html" ) ;
}
?>
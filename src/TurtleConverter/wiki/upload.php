<?php
#change to a dir path not accessible via browser.
$input_dir = "uploads/";
$output_dir = "outputs/";
$info_dir = "info/";
$info_file = "contentinfo.txt";
$convert_file_1 = "wikiconvert1.jar";
$convert_file_2 = "wikiconvert2.jar";
$wiki_page = "wiki.html";

# if form was submitted.
if(isset($_POST["submit"])) 
{
?>
<!DOCTYPE html>
<html>
<title>Uploading file</title>
<body>
<?php
    $uploadOk = 1;
    $fileType = pathinfo($_FILES["fileToUpload"]["name"],PATHINFO_EXTENSION);

    // Allow certain file formats
    if($fileType != "csv") 
    {
        echo "<p>Sorry, only CSV files are allowed.</p>";
        $uploadOk = 0;
    }    
    // Check file size
    if ($_FILES["fileToUpload"]["size"] > 500000000) 
    {
        echo "<p>Sorry, your file is too large.</p>";
        $uploadOk = 0;
    }

    // Check if $uploadOk is set to 0 by an error
    if ($uploadOk == 0) 
    {
        echo "<p>Sorry, your file was not uploaded.</p>";
    // if everything is ok, try to upload file
    } 
    else 
    {
        $input_file = $input_dir . $_FILES["fileToUpload"]["name"];
        if (move_uploaded_file($_FILES["fileToUpload"]["tmp_name"], $input_file)) 
        {
            echo "<p>The file ". basename($_FILES["fileToUpload"]["name"]). " has been uploaded.</p>";
            $command = "java -jar " . $convert_file_1 . " " . $input_file . " " . $info_dir;
            system($command);
            echo "<p>Notice: </p>";
            $handle = fopen($info_dir.$info_file, "r");
            if ($handle) 
            {
                echo "<ul>";
                while (($line = fgets($handle)) !== false) 
                {
                    echo "<li>".$line."</li>";
                }
                fclose($handle);
                echo "</ul>";
            } 
            else 
            {
                // error opening the file.
            }            
            echo "<p>Do you confirm? <a href='?action=confirm&file=".$_FILES["fileToUpload"]["name"]."'>Yes</a> or <a href='".$wiki_page."'>No</a>.</p>";            
        } 
        else 
        {
            echo "<p>Sorry, there was an error uploading your file.</p>";
        }
    }  
?>
</body>
</html>
<?php  
}
else if ($_GET['action'] == "confirm" && $_GET['file'] != "")
{
?>
<!DOCTYPE html>
<html>
<title>Uploading file</title>
<body>
<?php
    //$output_file = $output_dir . str_replace(".csv", ".ttl", $_GET['file']);
    $input_file = $input_dir . $_GET['file'];
    $unique_code = md5_file($input_file);
    mkdir($output_dir . $unique_code);
    $command = "java -jar " . $convert_file_2 . " " . $input_file . " " . $output_dir.$unique_code."/";
    system($command);
    echo "Directory: <a href='".$output_dir.$unique_code."'>view</a><br>";
?>
</body>
</html>
<?php  
}
# redirect to wiki page, if form was not submitted.
else
{
    header("Location: " . $wiki_page);
}
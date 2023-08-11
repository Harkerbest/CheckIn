<?php
session_start();

// 连接数据库
$servername = "数据库地址";
$username = "数据库用户名";
$password = "数据库密码";
$dbname = "数据库名";

$conn = new mysqli($servername, $username, $password, $dbname);

// 检查连接是否成功
if ($conn->connect_error) {
    die("数据库连接失败: " . $conn->connect_error);
}

// 获取题目
function getQuestions($conn) {
    $sql = "SELECT * FROM questions";
    $result = $conn->query($sql);
    $questions = $result->fetch_all(MYSQLI_ASSOC);

    foreach ($questions as &$question) {
        $question['correct_option'] = json_decode($question['correct_option']);
    }

    return $questions;
}

// 生成随机密码
function generateRandomPassword($length = 10) {
    $chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $password = '';
    for ($i = 0; $i < $length; $i++) {
        $index = mt_rand(0, strlen($chars) - 1);
        $password .= $chars[$index];
    }
    return $password;
}

// 保存密码到数据库
function savePassword($conn, $password) {
    $sql = "INSERT INTO users (password) VALUES ('$password')";
    $conn->query($sql);
}

// 计算总分和判断是否合格
function calculateScore($conn, $answers) {
    global $totalScore;
    $totalScore = 0;
    foreach ($answers as $questionId => $selectedOptions) {
        $sql = "SELECT question_type, correct_option FROM questions WHERE id=$questionId";
        $result = $conn->query($sql);
        $row = $result->fetch_assoc();
        $questionType = $row["question_type"];
        $correctOptions = json_decode($row["correct_option"]);

        // 多选题判断
        if ($questionType == 2) {
            $selectedOptions = array_map('intval', $selectedOptions);  // 将选项编号转为整数数组
            sort($selectedOptions);
            sort($correctOptions);

            if ($selectedOptions == $correctOptions) {
                $totalScore+=2;
            }
        }
        // 单选题判断
        else if ($questionType == 1) {
            $selectedOption = intval($selectedOptions);
            if (in_array($selectedOption, $correctOptions)) {
                $totalScore+=2;
            }
        }
    }

    if ($totalScore >= 60) { //合格分数设置为60分
        $password = generateRandomPassword();
        savePassword($conn, $password);
        return array("pass" => true, "password" => $password);
    } else {
        return array("pass" => false);
    }
}

// 获取题目并渲染网页
$questions = getQuestions($conn);

// 处理表单提交
$message = "";
$alertClass = "";
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $data = array(
        'secret' => "h-captcha secret",
        'response' => $_POST['h-captcha-response']
    );
    $verify = curl_init();
    curl_setopt($verify, CURLOPT_URL, "https://hcaptcha.com/siteverify");
    curl_setopt($verify, CURLOPT_POST, true);
    curl_setopt($verify, CURLOPT_POSTFIELDS, http_build_query($data));
    curl_setopt($verify, CURLOPT_RETURNTRANSFER, true);
    $response = curl_exec($verify);
    $responseData = json_decode($response);

    if ($responseData->success) {
        $answers = $_POST["answer"]; // 保存用户答案

        $_SESSION['answers'] = $answers; // 将答案保存到Session中

        $result = calculateScore($conn, $answers);

        if ($result["pass"]) {
            $password = $result["password"];
            $message = $totalScore . "/100 恭喜您合格！您的密码是：" . $password;
            $alertClass = "alert-success";
        } else {
            $message = $totalScore . "/100 测试未通过";
            $alertClass = "alert-danger";
        }
    } else {
        $message = "验证码验证错误";
        $alertClass = "alert-danger";
    }
} else {
    // 如果Session中存在答案，则从Session中恢复答案
    if (isset($_SESSION['answers'])) {
        $answers = $_SESSION['answers'];
    }
}

?>

<!DOCTYPE html>
<html>
<head>
    <title>测试标题</title>
    <script src='https://www.hCaptcha.com/1/api.js' async defer></script>
    <link rel="stylesheet" href="./bootstrap.min.css">
    <script src="./bootstrap.bundle.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        .container {
            padding: 20px;
            width: 100%;
            box-sizing: border-box;
        }

        .question {
            margin-bottom: 20px;
            word-wrap: break-word;
        }

        .options {
            margin-left: 20px;
        }

        .options label {
            display: block;
            margin-bottom: 10px;
        }

        input[type="submit"] {
            display: block;
            margin: 20px auto;
            padding: 10px 20px;
            background-color: #800080;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .alert {
            margin-bottom: 20px;
        }
    </style>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div class="container">
        <h2 class="text-center"><font color="#00BFFF">入群测试</font></h2>
        
        <br>
        <h6>
            前言
        </h6>
        <br>
        <hr>
        <br>

        <?php if ($_SERVER["REQUEST_METHOD"] == "POST") { ?>
            <div class="alert <?php echo $alertClass; ?>" role="alert">
                <h4 class="alert-heading"><?php echo $message; ?></h4>
                <?php if ($message == $totalScore . "/100 恭喜您合格！您的密码是：" . $password) {
                    echo "<hr>";
                    echo "<p class=\"mb-0\">请前往QQ群提交入群申请！</p>";
                 } 
                 else  if ($message == "验证码验证错误"){
                     echo "<hr>";
                     echo "<p class=\"mb-0\">请完成验证码验证！若验证码显示异常请切换网络</p>";
                 }
                else if ($message == $totalScore . "/100 测试未通过"){
                     echo "<hr>";
                     echo "<p class=\"mb-0\">兄弟你的悟性有待提高哦</p>";
                 }
                 ?>
                
            </div>
        <?php } ?>

        <form method="POST" action="" onsubmit="saveAnswers()">
            <?php foreach ($questions as $question) { ?>
                <div class="question">
                    <p><?php echo $question["question_content"]; ?></p>
                    <div class="options">
                        <?php
                        $questionId = $question['id'];
                        $inputType = ($question['question_type'] == 2) ? 'checkbox' : 'radio';

                        foreach (json_decode($question["options"]) as $index => $option) { 
                            $savedAnswer = isset($answers[$questionId]) ? $answers[$questionId] : '';
                            ?>
                            <div class="form-check">
                                <input class="form-check-input" type="<?php echo $inputType; ?>" name="answer[<?php echo $questionId; ?>]<?php echo ($inputType == 'checkbox') ? '[]' : ''; ?>" value="<?php echo $index; ?>" id="option_<?php echo $questionId . '_' . $index; ?>" <?php if ($savedAnswer && (($inputType == 'radio' && $savedAnswer == $index) || ($inputType == 'checkbox' && in_array($index, $savedAnswer)))) echo 'checked'; ?>>
                                <label class="form-check-label" for="option_<?php echo $questionId . '_' . $index; ?>">
                                    <?php echo $option; ?>
                                </label>
                            </div>
                        <?php } ?>
                    </div>
                </div>
            <?php } ?>
            <div class="h-captcha" data-sitekey="h-captcha sitekey"></div>
            <input type="submit" class="btn btn-primary" value="提交答案">
        </form>
        <p style="text-align:center;">由<a href=https://www.harkerbest.cn/>Harkerbest</a>开发与维护</p>
    </div>

    <script>
        function saveAnswers() {
            const answers = {};
            const form = document.forms[0];
            const elements = form.elements;
            
            for (let i = 0; i < elements.length; i++) {
                const element = elements[i];
                
                if (element.type === 'radio' && element.checked) {
                    const questionId = element.name.match(/\[(\d+)\]/)[1];
                    const value = element.value;
                    
                    if (!answers[questionId]) {
                        answers[questionId] = value;
                    }
                }
                
                if (element.type === 'checkbox' && element.checked) {
                    const questionId = element.name.match(/\[(\d+)\]/)[1];
                    const value = element.value;
                    
                    if (!answers[questionId]) {
                        answers[questionId] = [];
                    }
                    
                    answers[questionId].push(value);
                }
            }
            
            sessionStorage.setItem('answers', JSON.stringify(answers));
        }
        
        // 页面加载时，从Session Storage中恢复答案
        window.addEventListener('DOMContentLoaded', () => {
            const answers = JSON.parse(sessionStorage.getItem('answers'));
            
            if (answers) {
                const form = document.forms[0];
                const elements = form.elements;
                
                for (let i = 0; i < elements.length; i++) {
                    const element = elements[i];
                    
                    if (element.type === 'radio' || element.type === 'checkbox') {
                        const questionId = element.name.match(/\[(\d+)\]/)[1];
                        
                        if (answers[questionId]) {
                            if (element.type === 'radio') {
                                element.checked = (element.value === answers[questionId]);
                            } else if (element.type === 'checkbox') {
                                element.checked = answers[questionId].includes(element.value);
                            }
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>

<?php
$conn->close();
?>

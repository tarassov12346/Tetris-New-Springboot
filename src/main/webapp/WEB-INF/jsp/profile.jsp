<%@ page import="com.app.game.tetris.serviceImpl.State" %>
<%@ page import="com.app.game.tetris.serviceImpl.Stage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Tetris</title>

    <link href="/css/styleProfile.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <script src="<c:url value="/resources/jquery-3.6.0.min.js"/>"></script>
</head>
<body>

<h1 text align="center"> <b id="profileBox"> Hello ${player}! Your best score is ${playerBestScore}</b> </h1>
<h2 text align="center"> <b id="profileAttemptsBox"> You have played ${playerAttemptsNumber} times! </b> </h2>
<h3 text align="center"> <b id="profileAttemptsBox"> LAST PLAY RESULT ******************YOUR MUGSHOT ****************BEST PLAY RESULT</b> </h3>
<div id="controls">
   <button id="startGameButton" type="button" class="buttonStart" onclick="start()" >Start</button>
</div>

<img class="mugShot" src="/getPhoto" alt="" width="120" height="120">
<img class="snapShot" src="/getSnapShot" alt="" width="120" height="120">
<img class="snapShotBest" src="/getSnapShotBest" alt="" width="120" height="120">

<script>

function start() {
 window.location='/start';
}

</script>



</body>
</html>
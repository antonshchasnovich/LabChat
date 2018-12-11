<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
    <title>Agent-client chat</title>
    <style>
        .chatbox{
            display: none;
        }
        .messages{
            border-color: aqua;
            width: 500;
            padding: 20px;
        }
        .messages .msg{
            background-color: cornflowerblue;
            border-radius: 10px;
            margin-bottom: 10px;
            overflow: hidden;
        }
        .messages .msg .from{
            background-color: burlywood;
            line-height: 30px;
            text-align: center;
            color: white;
        }
        .messages .msg .text{
            padding: 10px;
        }
        textarea.msg{
            width: 540px;
            padding: 10px;
            resize: none;      
        }
    </style>
    
    <script>
    let chatUnit = {
        
        init(){
            this.type = "";
            
            this.startbox = document.querySelector(".start");
            this.chatbox = document.querySelector(".chatbox");
            this.buttonsbox = document.querySelector(".buttons");
            this.buttonsbox2 = document.querySelector(".buttons2");
            
            this.agentButton = this.buttonsbox.querySelector(".agentButton");
            this.clientButton = this.buttonsbox.querySelector(".clientButton");
            this.leaveButton = this.buttonsbox2.querySelector(".leaveButton");

            
            this.nameInput = this.startbox.querySelector("input");
            
            this.msgTextArea = this.chatbox.querySelector("textarea");
            this.chatMessageContainer = this.chatbox.querySelector(".messages");
            
            this.bindEvents();
        },
          
        bindEvents(){
        this.agentButton.addEventListener("click", e=>this.regAgent());
        this.clientButton.addEventListener("click", e=>this.regClient());
        this.leaveButton.addEventListener("click", e=>this.leave());
            this.msgTextArea.addEventListener("keyup",e=>{
                if(e.ctrlKey&&e.keyCode===13){
                    e.preventDefault();
                    this.send(this.msgTextArea.value);
                }
            })
            },
        
        send(){
            this.sendMessage({
                name:this.name,
                text:this.msgTextArea.value,
                type:this.type
            });
        },
        
        onOpenSock(){
            this.send();
            this.type = "TEXT_MESSAGE";
        },
        onMessage(msg){
            let msgBlock = document.createElement("div");
            msgBlock.className = "msg";
            let fromBlock = document.createElement("div");
            fromBlock.className = "from";
            fromBlock.innerText=msg.name;
            let textBlock = document.createElement("div");
            textBlock.className = "text";
            textBlock.innerText=msg.text;
            
            msgBlock.appendChild(fromBlock);
            msgBlock.appendChild(textBlock);
            this.chatMessageContainer.prepend(msgBlock);
            
        },
        onClose(){
            
        },
        sendMessage(msg){
          this.ws.send(JSON.stringify(msg));  
            this.msgTextArea.value="";
        },
        
                openSocket(){
                    this.ws = new WebSocket("ws://localhost:8080/Chat/chat");
                    this.ws.onopen = ()=>this.onOpenSock();
                    this.ws.onmessage = (e)=>this.onMessage(JSON.parse(e.data));
                    this.ws.onclose = (e)=>this.onClose();
                    this.name = this.nameInput.value;
                    this.startbox.style.display = "none";
                    this.chatbox.style.display = "block";
                },
        
        regAgent(){
            this.type = "AGENT_REG_MESSAGE";
            this.openSocket();
        },
            
        regClient(){
            this.type = "CLIENT_REG_MESSAGE";
            this.openSocket();
        },
        
        leave(){
            this.type = "LEAVE_MESSAGE";
            this.send();
            this.type = "TEXT_MESSAGE";
        }
    };
        
        
        window.addEventListener("load", e=>chatUnit.init());
    </script>
    
</head>
<body>
    <h1></h1>
    <div class="start">
        <input type="text" class="username" placeholder="enter name...">
        <div class="buttons">
            <button class="agentButton">agent</button>
            <button class="clientButton">client</button>
        </div>
    </div>
    <div class="chatbox">
      <div class="buttons2">
            <button class="leaveButton">leave</button>
        </div>
       <textarea class="msg">     
        </textarea>
        <div class="messages">
           
    </div>
        
    </div>
</body>
</html>
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
    font-family: cursive;
    word-wrap: normal;
    overflow-y: auto; /* Добавляем полосы прокрутки */
    width: 450px; /* Ширина блока */
    max-width: 500px;
    height: 600px; /* Высота блока */
    padding: 5px; /* Поля вокруг текста */
    border: solid 1px black; /* Параметры рамки */
   } 
        textarea.msg{
            width: 460px;
            padding: 10px;
            resize: none;      
        }
    </style>
    
    <script>
    let chatUnit = {
        
        init(){
            this.type = "";
            this.tabsNumber = 1;
            this.index = 0;
            this.activeTab = null;
            this.tabs = new Array(0);
            this.buttons = new Array(0);
            
            //tabs init
            this.tabsWrapper = document.querySelector(".tabsWrapper");
            this.numbersMenu = document.querySelector('#tabsNumber');
            
            
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
        this.numbersMenu.addEventListener("change", e=>this.changeTabsNumber());
            this.msgTextArea.addEventListener("keyup",e=>{
                if(e.ctrlKey&&e.keyCode===13){
                    e.preventDefault();
                    this.send(this.msgTextArea.value);
                }
            })
            },
        
        changeTabsNumber(){
            this.tabsNumber = this.numbersMenu.value;
        },
        
        send(){
            this.sendMessage({
                name:this.name,
                text:this.msgTextArea.value,
                type:this.type,
                index:this.index
            });
        },
        
        onOpenSock(){
            this.createTabs();
            this.index = this.tabsNumber;
            this.send();
            this.index = 0;
            this.type = "TEXT_MESSAGE";
        },
        onMessage(msg){
            let message = document.createElement("p");
            if(msg.type == "SERVER_MESSAGE"){
                message.style.color = "#9400D3";
            }
            else if(msg.type == "HISTORY_MESSAGE"){
                message.style.color = "#FF8C00";
            }
            else if(msg.name == this.name){
                message.style.color = "#0000FF";
            }
            else{
                message.style.color = "#008000";
            }
            message.innerText = msg.name + ": " + msg.text;
            this.tabs[msg.index].append(message);
            if(this.tabs[msg.index] != this.activeTab){
                this.buttons[msg.index].style.background = "coral";
            }
            this.chatMessageContainer.scrollTop = this.chatMessageContainer.scrollHeight;
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
            this.tabsNumber = 1;
            this.tabsWrapper.style.display = "none";
            this.type = "CLIENT_REG_MESSAGE";
            this.openSocket();
        },
        
        leave(){
            this.type = "LEAVE_MESSAGE";
            this.send();
            this.type = "TEXT_MESSAGE";
        },
        
        
        createTabs(){
            for(index = 0; index < this.tabsNumber; index++){
                var i = index;
                let button = document.createElement("button");
                button.innerText = "Tab-" + index;
                button.style.height = "40px";
                button.style.width = "80px";
                this.tabsWrapper.append(button);
                let text = document.createElement("div");
                text.class = index;
                this.chatMessageContainer.append(text);
                this.tabs[index] = text;
                this.buttons[index] = button;
                button.addEventListener("click", e=>this.showTab(text, button));   
            }
            this.showTab(this.tabs[0], this.buttons[0]);
        },
        
        showTab(text, button){
            for(i = 0; i < this.tabsNumber; i++){
                this.tabs[i].style.display = "none";
                if(this.tabs[i] == text){
                    this.index = i;
                }
            }
            button.style.background = "white";
            text.style.display = "block";
            this.chatMessageContainer.scrollTop = this.chatMessageContainer.scrollHeight;
            this.activeTab = text;
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
            <select id="tabsNumber">
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
                <option value="6">6</option>
            </select>
        </div>
    </div>
    <div class="chatbox">
      <div class="buttons2">
        <div class="messages"></div>
        <button class="leaveButton">leave</button>
        </div>
       <textarea class="msg">     
        </textarea>  
    </div>
    
    
    <div class="tabsWrapper">
        
    </div>
</body>
</html>
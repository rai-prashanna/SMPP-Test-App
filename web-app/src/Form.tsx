import React, { useEffect, useRef, useState ,} from "react";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Divider } from "primereact/divider";
import { InputTextarea } from 'primereact/inputtextarea';
import type { SubmitSmResult } from "./model/SubmitSmResp";
// import { useWebSocket } from "./useWebSocket";

import SockJS from "sockjs-client";
import { Client, over } from "stompjs";

export interface Message {
  id: string;
  submitted: number;
  delivered: number;
  submitDate: number;
  doneDate: number;
  finalStatus: string;
  error: string;
  text: string;
}

async function submitSMAApi(msg: string) {
  const data = {
    message: msg,
  };

  const response = await fetch("http://localhost:8080/submit-msg", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
  const submitSMres = await response.json();
  return submitSMres;
}

const Form: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [submmitSMResp, setSubmmitSMResp] = useState<SubmitSmResult | null>(null);
  // const messages = useWebSocket("/ws", "/queue/messages");
  const [messages, setMessages] = useState<Message[]>([]);


  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws");
    const client: Client = over(socket);

    client.connect({}, () => {
      console.log("Connected to WebSocket");

      client.subscribe("/queue/messages", (payload) => {
        console.log("Received message:", payload.body);
        const msg: Message = JSON.parse(payload.body);
        setMessages((prev) => [...prev, msg]);
      });
    });

    return () => {
      console.log("WebSocket disconnected");
    };
  }, []);

  function submmitSM() {
    setLoading(true);
    submitSMAApi(message).then((submmitSMResp) => {
      setLoading(false);
      setSubmmitSMResp(submmitSMResp);
      console.log("Submit successful:", JSON.stringify(submmitSMResp));
    })    .catch((err) => {
      console.error("Submit failed:", err);
      setLoading(false); 
    });;

  }


  return (
    <div>
      <div className="flex flex-wrap align-items-center mb-3">
        <InputTextarea id="message"
          name="message"
          placeholder="Message" value={message} onChange={(e) => setMessage(e.target.value)} rows={5} cols={30} />

      </div>

      <div className="flex flex-wrap">
        <Button
          label="Submit"
          icon="pi pi-check"
          loading={loading}
          onClick={submmitSM}
        />
      </div>
      <Divider />
          <div>
      <h2>WebSocket Messages</h2>

      {messages.length === 0 && <p>No messages yet...</p>}

      <ul>
        {messages.map((msg, i) => (
          <li key={i}>{msg.finalStatus}</li>
        ))}
      </ul>
    </div>
    </div>
  );
};

export default Form;



import React, { useEffect, useState, useRef } from "react";
import { Button } from "primereact/button";
import { Divider } from "primereact/divider";
import { InputTextarea } from "primereact/inputtextarea";
import SockJS from "sockjs-client";
import { Client, over } from "stompjs";
import { DataTable } from "primereact/datatable";
import { Column } from "primereact/column";

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
  //const [submmitSMResp, setSubmmitSMResp] = useState<SubmitSmResult | null>(null);
  // const messages = useWebSocket("/ws", "/queue/messages");
  const [messages, setMessages] = useState<Message[]>([]);

  const connectedRef = useRef(false);

  useEffect(() => {
    if (connectedRef.current) return; // prevents duplicate subscription in React StrictMode
    connectedRef.current = true;

    const socket = new SockJS("http://localhost:8080/ws");
    const client: Client = over(socket);

    client.connect({}, () => {
      console.log("Connected to WebSocket");

      client.subscribe("/topic/messages", (payload) => {
        const messages: Message[] = JSON.parse(payload.body);
        console.log("Received messages:", messages);
        setMessages((prev) => [...prev, ...messages]);
      });
    });

    return () => {
      console.log("Disconnecting WebSocket...");
      if (client?.connected) {
        client.disconnect(() => console.log("Disconnected"));
      }
    };
  }, []);

  function submmitSM() {
    setLoading(true);
    submitSMAApi(message)
      .then((submmitSMResp) => {
        setLoading(false);
        console.log("Submit successful:", JSON.stringify(submmitSMResp));
      })
      .catch((err) => {
        console.error("Submit failed:", err);
        setLoading(false);
      });
  }

  return (
    <div>
      <div className="flex flex-wrap align-items-center mb-3">
        <InputTextarea
          id="message"
          name="message"
          placeholder="Message"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          rows={5}
          cols={30}
        />
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
        <h2>Delivery Receipt messages</h2>

        {messages.length === 0 && <p>No messages yet...</p>}

        {messages.length !== 0 && (
          <div className="card">
            <DataTable
              value={messages}
              showGridlines
              tableStyle={{ minWidth: "50rem" }}
            >
              <Column field="id" header="Id"></Column>
              <Column field="submitted" header="Submitted"></Column>
              <Column field="delivered" header="Delivered"></Column>
              <Column field="submitDate" header="SubmitDate"></Column>
              <Column field="doneDate" header="DoneDate"></Column>
              <Column field="finalStatus" header="FinalStatus"></Column>
              <Column field="error" header="Error"></Column>
              <Column field="text" header="Text"></Column>
            </DataTable>
          </div>
        )}
        {/* <ul>
        {messages.map((msg, i) => (
          <li key={i}>{msg.finalStatus}</li>
        ))}
      </ul> */}
      </div>
    </div>
  );
};

export default Form;

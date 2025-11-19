import { useEffect, useState, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp, { Client } from "stompjs";

export default function Chat() {
  const [messages, setMessages] = useState<string[]>([]);
  const inputRef = useRef<HTMLInputElement>(null);
  const stompClientRef = useRef<Client | null>(null);

  useEffect(() => {
    // 1. Create WebSocket
    const socket = new SockJS("http://localhost:8080/ws");

    // 2. Create STOMP client
    const stompClient = Stomp.over(socket);
    stompClientRef.current = stompClient;

    // 3. Connect & subscribe
    stompClient.connect({}, (frame) => {
      console.log("Connected: " + frame);

      stompClient.subscribe("/topic/messages", (payload) => {
        setMessages((prev) => [...prev, payload.body]); // append message
      });
    });

    // 4. Cleanup on unmount
    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.disconnect(() => {
          console.log("Disconnected");
        });
      }
    };
  }, []);

  const sendMessage = () => {
    if (!inputRef.current) return;
    
    const msg = inputRef.current.value;
    if (!msg.trim()) return;

    if (!stompClientRef.current) return;

    stompClientRef.current.send(
      "/app/chat",
      {},
      JSON.stringify({ content: msg })
    );

    inputRef.current.value = "";
  };

  return (
    <div style={{ width: "400px", margin: "20px auto", fontFamily: "Arial" }}>
      <h2>React STOMP Chat</h2>

      <ul id="message-list">
        {messages.map((msg, idx) => (
          <li key={idx}>{msg}</li>
        ))}
      </ul>

      <input
        id="message-input"
        type="text"
        ref={inputRef}
        style={{ width: "70%", marginRight: "10px" }}
      />

      <button onClick={sendMessage}>Send</button>
    </div>
  );
}

import React, { useState } from "react";
import { Message } from "primereact/message";
import { InputText } from "primereact/inputtext";
import { Button } from "primereact/button";
import { Divider } from "primereact/divider";
import { InputTextarea } from 'primereact/inputtextarea';
import type { SubmitSmResult } from "./model/SubmitSmResp";


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
    </div>
  );
};

export default Form;


docker run -d --name activemq -p 61616:61616 -p 8161:8161 -p 61613:61613 apache/activemq-classic:6.1.8
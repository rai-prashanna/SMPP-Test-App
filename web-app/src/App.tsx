
import { Accordion, AccordionTab } from 'primereact/accordion';
import Form from './Form.js';

function App() {

  return (
    <>
        <div className="card">
            <Accordion activeIndex={0}>
                <AccordionTab header="Submit SMPP Message">
                  <Form />
                </AccordionTab>
            </Accordion>
        </div>
    </>
  )
}

export default App

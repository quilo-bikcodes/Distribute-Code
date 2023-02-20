import { useState } from "react";
import "./App.css";
import sample_img from "./assets/main.png";
import bg_img from "./assets/bg.png";
import Slider from "@mui/material/Slider";

function App() {
  const [value, setValue] = useState(19 * 3.6);
  const handleChange = (event, newValue) => {
    setValue(newValue * 3.6);
    // console.log(value);
  };
  const defaultstyle = {
    filter: `hue-rotate(${value}deg)`,
  };

  return (
    <div>
      <div className="center">
        <div className="main-body">
          <div className="card-container">
            <div className="card">
              <header>
                <h1>Use the slider to change the color of the image</h1>
              </header>
              <div className="image-wrapper">
                <img
                  src={sample_img}
                  alt="Used to be an image"
                  style={defaultstyle}
                />
              </div>
              <div className="slider">
                <div className="slider-wrapper">
                  <Slider
                    aria-label="Temperature"
                    defaultValue={19}
                    onChange={handleChange}
                    color="primary"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;

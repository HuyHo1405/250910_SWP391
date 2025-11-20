# Chat Bot Integration Guide

This README provides instructions to set up and run the chat bot integration for this project.

## Prerequisites

- **Python 3.8+** must be installed on your system.
- Recommended: Use a virtual environment for Python dependencies.

## Setup Steps

1. **Install Python**
   - Download and install Python from [python.org](https://www.python.org/downloads/).
   - Ensure `python` and `pip` are available in your system PATH.

2. **Create a Virtual Environment (Optional but recommended)**
   ```powershell
   python -m venv venv
   .\venv\Scripts\activate
   ```

3. **Install Required Python Packages**
   - Navigate to the `chat-bot` directory:
     ```powershell
     cd src\chat-bot
     ```
   - Install dependencies (update as needed):
     ```powershell
     pip install flask requests
     ```
   - If you have a `requirements.txt`, use:
     ```powershell
     pip install -r requirements.txt
     ```

4. **Run the Chat Bot Server**
   - Start the Python server:
     ```powershell
     python app.py
     ```
   - Ensure the server is running and accessible (default: `http://localhost:5000`).

5. **Integration with Java**
   - The Java backend calls the Python bot via HTTP requests.
   - Make sure the Python server is running before using chat features in the Java app.

## Troubleshooting

- If you encounter issues, check:
  - Python version compatibility
  - Required packages are installed
  - Server is running and not blocked by firewall

## Updating Dependencies

- Add new Python packages as needed:
  ```powershell
  pip install <package-name>
  pip freeze > requirements.txt
  ```

## Notes

- For production, consider using process managers (e.g., `gunicorn`, `supervisor`).
- Update this README if you add new features or dependencies.

---

For further questions, contact the project maintainer.


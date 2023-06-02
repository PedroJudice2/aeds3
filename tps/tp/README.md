# Project Name

This project is about data manipulation in a indexed file

## Getting Started

To get started with this project, follow these steps:

1. Clone the repository.
2. Install the dependencies.
3. Run the application.

## Usage

To use this application, follow these steps:

1. Open the application.
2. Choose if 
3. Click the "Login" button.

## File Header Meaning

The file header contains the following information:

- First 4 bytes (int):
  - 0 if is not compressed
  - 1 if is compressed by huffman
  - 2 if is compressed by LZW
- Second 4 bytes (int):
  - 1 if is b plus tree
  - 2 if is extensible hashing
  - 3 if is external sorting.
- Third 8 bytes (long):
  - Last movie id
- Fourth 4 bytes (int):
  - Number of movies

## Contributing

To contribute to this project, follow these steps:

1. Fork the repository.
2. Create a new branch.
3. Make your changes.
4. Push your changes to your fork.
5. Submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
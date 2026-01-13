# Sample JSON files for testing

## simple.json
{"name": "John", "age": 25}

## array.json  
[1, 2, 3, "hello", true, null]

## nested.json
{
  "user": {
    "name": "Alice",
    "profile": {
      "age": 30,
      "city": "Boston"
    }
  },
  "active": true,
  "tags": ["admin", "user"]
}

## tasks.json (Task-Tracker format)
[
  {
    "id": 1,
    "description": "Learn JSON parsing",
    "status": "in-progress",
    "createdAt": "2024-01-01",
    "updatedAt": "2024-01-01"
  },
  {
    "id": 2,
    "description": "Build parser",
    "status": "todo",
    "createdAt": "2024-01-01",
    "updatedAt": "2024-01-01"
  }
]

## edge-cases.json
{
  "empty_string": "",
  "escaped": "Hello \"World\"",
  "number_int": 42,
  "number_float": 3.14,
  "number_negative": -10,
  "empty_object": {},
  "empty_array": [],
  "null_value": null
}

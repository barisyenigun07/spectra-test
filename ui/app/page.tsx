import { Client } from "@stomp/stompjs";
import Image from "next/image";

type User = {
  id: number,
  name: string,
  age: number,
  job: string
}

const users: User[] = [
  {
    id: 1,
    name: "John Doe",
    age: 35,
    job: "Software Developer"
  },
  {
    id: 2,
    name: "George Wilkinson",
    age: 46,
    job: "Doctor"
  },
  {
    id: 3,
    name: "Alice Warburton",
    age: 27,
    job: "Accountant"
  },
  {
    id: 4,
    name: "Susie Richards",
    age: 34,
    job: "Lawyer"
  }
]

export default function Home() {
  return (
    <div>
      <div className="flex justify-center px-2 py-5 gap-3">
        {users.map((user) => (
          <div key={user.id} className="bg-amber-500 rounded-2xl shadow-xl w-52 px-2 py-4 m-2 hover:bg-red-700 transition-colors duration-300 ease-in-out">
            <p>Name: {user.name}</p>
            <p>Age: {user.age}</p>
            <p>Job: {user.job}</p>
          </div>
        ))}
      </div>
      <div className="flex justify-center px-2 py-5 gap-3">
        <div className="bg-purple-700 rounded-2xl shadow-xl w-52 px-2 py-4 m-2">
          <p>Name</p>
          <p>Age</p>
          <p>Job</p>
        </div>
        <div className="bg-purple-700 rounded-2xl shadow-xl w-52 px-2 py-4 m-2">
          <p>Name</p>
          <p>Age</p>
          <p>Job</p>
        </div>
        <div className="bg-purple-700 rounded-2xl shadow-xl w-52 px-2 py-4 m-2">
          <p>Name</p>
          <p>Age</p>
          <p>Job</p>
        </div>
        <div className="bg-purple-700 rounded-2xl shadow-xl w-52 px-2 py-4 m-2">
          <p>Name</p>
          <p>Age</p>
          <p>Job</p>
        </div>
      </div>
    </div>
  );
}
